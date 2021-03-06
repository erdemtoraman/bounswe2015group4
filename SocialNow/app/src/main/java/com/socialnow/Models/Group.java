package com.socialnow.Models;

import java.io.Serializable;

import javax.crypto.SecretKey;

/**
 * Created by mugekurtipek on 06/12/15.
 */
public class Group implements Serializable {


    String type;

    String owner_token;

    String tags;

    String visibleTo;

    private long id;

    //private String tags = "";

    String group_description;

    User owner;

    User[] group_member_users;

    String group_name;

    public  String group_members ;

    String group_posts;
    public  String group_photo ;
    public Group(){

    }
    public Group(String group_name,String visibleTo, User[] group_member_users,Long id, String type, String group_photo, String group_comments,String group_description,String group_members,String tags,String owner_token, String group_posts){
        this.id = id;
        this.group_description = group_description;
        this.owner_token = owner_token;
        this.group_name = group_name;
        this.group_members = group_members;
        this.tags = tags;
        this.group_posts = group_posts;
        this.group_photo =group_photo;
        this.type = type;
        this.group_member_users =group_member_users;
        this.visibleTo = visibleTo;
    }


    //public  String group_posts ;

    public String getGroup_photo() {
        return group_photo;
    }

    public void setGroup_photo(String group_photo) {
        this.group_photo = group_photo;
    }


    public String get_visibleTo() {
        return visibleTo;
    }

    public void set_visibleTo(String visibleTo) {
        this.visibleTo = visibleTo;
    }

    public String getGroup_tags() {
        return tags;
    }

    public void setGroup_tags(String event_tags) {
        this.tags = event_tags;
    }

    public void setOwner_token(String group_owner_token) {
        this.owner_token = group_owner_token;
    }
    public String getOwner_token() {
        return this.owner_token;
    }

    public String getGroup_description() {
        return group_description;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }


   /* public Group(Interest_Group_Form form) {
        this.owner_token = form.getOwner_token();
        if (form.getTags().startsWith(",")){
            this.tags = form.getTags();
        }
        else{
            this.tags = ","+form.getTags();
        }
        this.group_description=form.getGroup_description();
        this.setGroup_members("");
        this.setGroup_name(form.getGroup_name());
        this.setGroup_posts("");
        this.setGroup_photo(form.getGroup_photo());
    }*/


    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroup_members() {
        return group_members;
    }

    public void setGroup_members(String group_members) {
        this.group_members = group_members;
    }


   /* public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    public String getGroup_posts() {
        return group_posts;
    }

    public void setGroup_posts(String group_posts) {
        this.group_posts = group_posts;
    }*/

    public String getGroup_posts() {
        return group_posts;
    }

    public void setGroup_posts(String group_posts) {
        this.group_posts = group_posts;
    }
}
