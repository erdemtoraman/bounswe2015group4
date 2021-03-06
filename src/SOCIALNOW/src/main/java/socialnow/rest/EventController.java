package socialnow.rest;

import com.google.gson.*;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socialnow.SemanticTagging.SemanticResponse;
import socialnow.Utils.Error_JSON;
import socialnow.Utils.RequestSender;
import socialnow.Utils.Util;
import socialnow.dao.CommentDao;
import socialnow.dao.EventDao;
import socialnow.dao.PostDao;
import socialnow.dao.UserDao;
import socialnow.forms.Event.Add_Post_Event_Form;
import socialnow.forms.Event.Event_Form;
import socialnow.forms.User.User_Token_Form;
import socialnow.model.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * Event related requests are here
 */
@RestController
public class EventController {
    @Autowired
    private EventDao eventDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;
    Logger log = Logger.getLogger("EVENTCONTROLLER");
    // Creates the json object which will manage the information received
    Gson gson = new GsonBuilder()
            .setDateFormat("dd/MM/yyyy hh:mm")
            .create();

    /**
     * creates an event with the parameters given in request.
     * @param addEventForm
     * @return
     */
    @RequestMapping( value = "/createEvent", method = RequestMethod.POST)
    public @ResponseBody
    Event addEvent(@RequestBody String addEventForm) {
        Event_Form form = gson.fromJson(addEventForm, Event_Form.class);
        Event e = new Event(form);
        User u = userDao.getByToken(e.getEvent_host_token());
        u.setUser_tags(u.getUser_tags() + e.getTags());
        e.event_participants= (e.event_participants+"," + e.getEvent_host_token());
        eventDao.create(e);
        u.setUser_participating_events(u.getUser_participating_events()+","+e.getId());
        userDao.update(u);
        return e;
    }

    /**
     * adds a created post to an event, updates event posts
     * @param addPostEventForm
     * @return
     */
    @RequestMapping( value = "/events/addPost", method = RequestMethod.POST)
    public @ResponseBody
    Event addPost(@RequestBody String addPostEventForm) {

        Add_Post_Event_Form apef = gson.fromJson(addPostEventForm, Add_Post_Event_Form.class);
        Event e = eventDao.getById(apef.getEvent_id());
        e.setEvent_posts(e.getEvent_posts()+","+ apef.getPost_id());
        eventDao.update(e);
        return e;
    }

    /**
     * semantic tagging related, returns exactly what wikidata would return
     * @param search
     * @return
     * @throws UnirestException
     */
    @RequestMapping( value = "/deneme", method = RequestMethod.POST)
    public @ResponseBody
    String FOOO(@RequestBody String search) throws UnirestException {
        return  RequestSender.searchSemantics(search);
    }

    /**
     * adds a user to an event, updates both event and user.
     * @param token
     * @return
     */
    @RequestMapping(value = "/events/addParticipant", method = RequestMethod.POST)
    public @ResponseBody Event addParticipant(@RequestBody String token){
        User_Token_Form form = gson.fromJson(token,User_Token_Form.class);
        User user =userDao.getByToken(form.getUser_token());
        Event event =  eventDao.getById(form.getEvent_id());
        if(Util.arrayContains(user.getUser_participating_events().split(","), event.getId()+"")){
            Error_JSON errorJSON = new Error_JSON();
            errorJSON.setCode(8082);
            errorJSON.setMessage("This user is already participating to this event ");
            return new Event(errorJSON);
        }
        user.setUser_participating_events(user.getUser_participating_events()+"," + form.getEvent_id());
        event.event_participants= (event.event_participants+"," + form.getUser_token());

        user.setUser_tags(user.getUser_tags() + event.getTags());
        try{
            userDao.update(user);
            eventDao.update(event);

        }catch (Exception e) {
            Error_JSON errorJSON = new Error_JSON();
            errorJSON.setCode(8081);
            errorJSON.setMessage("DATABASE ERROR: " + e.toString());
            return new Event(errorJSON);
        }
        return  this.fillEvent(event);
    }


    /**
     * removes a user from an event, updates both event and user
     * @param token
     * @return
     */
    @RequestMapping(value = "/events/removeParticipant", method = RequestMethod.POST)
    public @ResponseBody Event removeParticipant(@RequestBody String token){
        User_Token_Form form = gson.fromJson(token,User_Token_Form.class);
        User user =userDao.getByToken(form.getUser_token());
        Event event =  eventDao.getById(form.getEvent_id());
        log.info(event.getId()+"");
        log.info(user.getUser_participating_events());
        if(!Util.arrayContains(user.getUser_participating_events().split(","), event.getId()+"")){
            Error_JSON errorJSON = new Error_JSON();
            errorJSON.setCode(8082);
            errorJSON.setMessage("This user is not  participating to this event ");
            return new Event(errorJSON);
        }
        log.info(Util.deleteFromArray(user.getUser_participating_events().split(","),event.getId()+""));
        user.setUser_participating_events(Util.deleteFromArray(user.getUser_participating_events().split(","),event.getId()+""));
        event.event_participants = ( Util.deleteFromArray( event.event_participants.split(","), form.getUser_token()));
        user.setUser_tags(Util.deleteFromArray(user.getUser_tags().split(","),   event.getTags().split(",")));
        try{
            userDao.update(user);
            eventDao.update(event);

        }catch (Exception e) {
            Error_JSON errorJSON = new Error_JSON();
            errorJSON.setCode(8081);
            errorJSON.setMessage("DATABASE ERROR:" + e.toString());
            return new Event(errorJSON);
        }
        return  this.fillEvent(event);
    }


    /**
     * lists all events in the database with small extension of Event class
     * @return
     */
    @RequestMapping( value = "/listAllEvents", method = RequestMethod.POST)
    public @ResponseBody
    List<Event> listAllEvents(@RequestBody String usertoken){
        User_Token_Form form = gson.fromJson(usertoken, User_Token_Form.class);
        User u = userDao.getByToken(form.getUser_token());
        List<Event> eventList  = eventDao.getAllForUser(u);
        List<Event> eventlistFilled = new ArrayList<Event>();
        for (Event event : eventList) {
            eventlistFilled.add(this.fillEvent(event));
        }
        return  eventlistFilled;
    }

    /**
     * lists all events created by a specific user
     * @param token
     * @return
     */
    @RequestMapping( value = "/listMyEvents", method = RequestMethod.POST)
    public @ResponseBody
    List<Event> listEventOfUser(@RequestBody String token){
        Event_Form form = gson.fromJson(token, Event_Form.class);
        List<Event> eventList  = eventDao.getAllByToken(form.getEvent_host_token());
        List<Event>    eventlistFilled = new ArrayList<Event>();
        for (Event event :
                eventList) {
            eventlistFilled.add(this.fillEvent(event));
        }
        return  eventlistFilled;
    }

    /**
     * lists all the events that a user attends
     * @param token
     * @return
     */
    @RequestMapping( value = "/listAttendingEvents", method = RequestMethod.POST)
    public @ResponseBody
    List<Event> listEventsParticipated(@RequestBody String token){
        List<Event> eventList = new ArrayList<Event>();
        User_Token_Form form = gson.fromJson(token, User_Token_Form.class);
        User user = userDao.getByToken(form.getUser_token());
        log.info((user.getUser_participating_events()));
        if(user.getUser_participating_events()=="")
            return new ArrayList<Event>();
        String[] eventIdArray = user.getUser_participating_events().split(",");


        for (int i = 0; i <eventIdArray.length; i++) {

            try {
                int id = Integer.parseInt(eventIdArray[i]);
                Event e = eventDao.getById(id);
                eventList.add( this.fillEvent(e));
            }catch (Exception e){
            }
        }
        return eventList;
    }

    /**
     * returns the extended version of Event,
     * opens up all other sub objects and gives their details as well
     * @param eventId
     * @return
     */
    @RequestMapping( value = "/events/getEventDetail", method = RequestMethod.POST)
    public @ResponseBody
    EventDetail getEventDetails(@RequestBody String eventId){

        Add_Post_Event_Form form = gson.fromJson(eventId, Add_Post_Event_Form.class);
        Event event = eventDao.getById(form.getEvent_id());
        EventDetail eventDetail = new EventDetail(event);
        User user = userDao.getByToken(event.getEvent_host_token());
        eventDetail.setEvent_host(user);
        String [] participantId = event.event_participants.split(",");
        ArrayList<User> users = new ArrayList<User>();
        for (int i = 0; i <participantId.length ; i++) {
            if (!participantId[i].equals("")) {
                users.add(userDao.getByToken(participantId[i]));
            }
        }

        participantId= event.getEvent_posts().split(",");
        eventDetail.setEvent_participants(users);
        ArrayList<PostDetail> posts = new ArrayList<>();

        for (int i = 0; i <participantId.length ; i++) {
            if (!participantId[i].equals("")) {
                Post post = postDao.getById(participantId[i]);
                String comments = post.getPost_comments();
                List<Comment_Details> COMMENT_DETAILS_LIST = new ArrayList<>();
                if(comments.contains(",")){
                    comments = comments.substring(1);
                    String[] commentArray = comments.split(",");

                    for (int j = 0; j <commentArray.length ; j++) {
                        String  commentId = commentArray[j];
                        Comment cm = commentDao.getById(commentId);
                        Comment_Details cm_details = new Comment_Details(cm);
                        cm_details.setOwner(userDao.getByToken(cm.getOwner_token()));
                        COMMENT_DETAILS_LIST.add(cm_details);
                    }
                }
                PostDetail postDetail = new PostDetail(post);
                postDetail.setOwner(userDao.getByToken(post.getOwner_token()));
                postDetail.setComments(COMMENT_DETAILS_LIST);
                posts.add(postDetail);
            }
        }
        Collections.reverse(posts);
        eventDetail.setEvent_posts(posts);
        String tag = event.getTags();
        if(tag.contains(",")) {
            ArrayList<String > tags= new ArrayList<String>(Arrays.asList(tag.substring(1).split(",")));
            eventDetail.setTags(tags);
        }

        return eventDetail;
    }

    /**
     * an util method to open up users of an event
     * @param event
     * @return
     */
    public  Event fillEvent(Event event){
        String [] participantId = event.event_participants.split(",");
        List<User> users = new ArrayList<User>();
        for (int i = 0; i <participantId.length ; i++) {
            if (!participantId[i].equals("")) {

                users.add(userDao.getByToken(participantId[i]));
            }
        }
        event.fillUsers(users);

        return event;
    }

}
