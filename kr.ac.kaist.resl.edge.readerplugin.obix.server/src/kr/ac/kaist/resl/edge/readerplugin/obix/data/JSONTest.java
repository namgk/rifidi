package kr.ac.kaist.resl.edge.readerplugin.obix.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class JSONTest {

	public JSONTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		LocationValue l = new LocationValue("2304", "234");
		Gson gson = new Gson();
		System.out.println(gson.toJson(l));

		InputStream i = JSONTest.class.getResourceAsStream("value.test2.json");
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		try {
		LocationValue l2 = gson.fromJson(br, LocationValue.class);
		System.out.println((l2 == null) ? "l2 null" : gson.toJson(l2));
		} catch (JsonSyntaxException j){
			System.out.println("json syntax wrong");
		}

	}

}
