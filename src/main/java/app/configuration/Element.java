package app.configuration;

public class Element {
	public static final int OTHER = 0;
	public static final int ARTICLE = 1;
	public static final int INPROCEEDINGS=2;
	public static final int PROCEEDINGS=3;
	public static final int BOOK = 4;
	public static final int INCOLLECTION = 5;
	public static final int PHDTHESIS = 6;
	public static final int MASTERTHESIS = 7;
	public static final int WWW = 8;
	public static final int PERSON = 9;
	public static final int DATA = 10;
	
	public static boolean contains(String tipo) {
		return getElement(tipo) > 0;
	}

	public static int getElement(String name) {
		int res = -1;

		switch (name.toLowerCase().trim()) {
			case "article":
				res = 1;
				break;
	
			case "inproceedings":
				res = 2;
				break;
	
			case "proceedings":
				res = 3;
				break;
	
			case "book":
				res = 4;
				break;
	
			case "incollection":
				res = 5;
				break;
			
			case "phdthesis":
				res = 6;
				break;
			case "mastersthesis":
				res = 7;
				break;
			case "www":
				res = 8;
				break;
			case "person":
				res = 9;
				break;
			case "data":
				res = 10;
				break;	
			
			default :
				res = 0;
				break;
		}

		return res;
	}
	
	public static String getElementName(int indice) {
		String res = "";
		
		switch (indice) {

		case 1:
			res = "article";
			break;
		case 2:
			res = "inproceedings";
			break;
		case 3:
			res = "proceedings";
			break;
		case 4:
			res = "book";
			break;
		case 5:
			res = "incollection";
			break;
		case 6:
			res = "phdthesis";
			break;
		case 7:
			res = "mastersthesis";
			break;
		case 8:
			res = "www";
			break;
		case 9:
			res = "person";
			break;
		case 10:
			res = "data";
			break;

		default:
			res = "other";
			break;
		}
		
		return res;
	}

}
