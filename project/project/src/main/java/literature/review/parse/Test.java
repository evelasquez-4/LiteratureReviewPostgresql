package literature.review.parse;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

public class Test {

	
	  public static void main(String[] args) { // TODO Auto-generated method stub
	  
	  DBConnect conn = null;
	  Map<Integer, String> j = new HashMap<Integer,String>();
	  j.put(1, "value 1"); j.put(2, "value 2");
	  
	  int numero = 10;
	  
	  try { PreparedStatement st = conn.getInstance().getConnection().
	  prepareStatement("INSERT INTO slr.test(CADENA,NUMERO) VALUES(to_json(?::json),?)"
	  ); st.setString(1, new JSONObject(j).toString()); st.setInt(2, numero);
	  
	  st.executeUpdate(); st.close();
	  
	  } catch (SQLException e) { // TODO Auto-generated catch block
	  e.printStackTrace(); System.out.println(e.getMessage()); } }
	 

}
