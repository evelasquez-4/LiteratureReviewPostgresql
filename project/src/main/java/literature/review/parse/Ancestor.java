package literature.review.parse;

import java.util.ArrayList;
import java.util.List;

public class Ancestor {
	
	private String name;
	private List<String> elements;
	
	public Ancestor()
	{
		name = "";
		this.elements = new ArrayList<String>();
	}
	
	public Ancestor(String name, ArrayList<String> elements) {
		this.name = name;
		this.elements = elements;
	}
	
	public boolean contains(String name)
	{
		return getElement(name) >= 0;
	}
	
	public int getElement(String name) {
		return elements.indexOf(name);
	}
	public String getElement(int i) {
		return elements.get(i);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getElements() {
		return elements;
	}

	public void setElements(List<String> elements) {
		this.elements = elements;
	}
	
	public String toString() {
		return "name :"+getName()+"\nElements: "+getElements();
	}
	
}
