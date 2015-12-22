import java.util.*;

public class tagcl {
	String tag;
	Map<Integer,Integer> hasIt;
	tagcl(String a)
	{	tag=a;
		hasIt= new HashMap<Integer, Integer>();
	}
	public void person(int p)
	{	int b=1;
		if(hasIt.containsKey(p))
		{	b=hasIt.get(p);
			b+=1;
		} 
		hasIt.put(p, b);
	}
	public int hmpeople()
	{	return hasIt.size();
	}
	public double logfreq(int p)
	{	if(!hasIt.containsKey(p))
			return -1;
		int b;
		b=hasIt.get(p);
		return 1+Math.log(b);
	}
}