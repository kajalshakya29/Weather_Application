package MyPackage1;

import jakarta.servlet.ServletException;
import java.net.MalformedURLException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet1
 */
//@WebServlet("/MyServlet")
public class MyServlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet1() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
        response.sendRedirect("index.html");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		// API SETUP
		String apiKey1 = "fd745ab3c97c0e5d4b5aa008a15005f4";
		
		// Second API Key
	//	String apiKey2 = "a2fa76fde49e2fd48bbb2b34b218cb07"; 
		// Get the city from the form input
        String city = request.getParameter("city"); 

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey1;
    //    String apiUrl1 = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey2;
	 
        //Api URL 
        try {
        	
        	// Register Oracle JDBC driver
           // Class.forName("oracle.jdbc.driver.OracleDriver");
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            //reading the data from network
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
           
            // System.out.println(reader);
           
            StringBuilder responseContent = new StringBuilder();
            //to get input reader will create scanner object
            Scanner scanner = new Scanner(reader);
            
            while(scanner.hasNext()) {
            	responseContent.append(scanner.nextLine());
            }
            scanner.close();
            //System.out.println(responseContent);
            //typecasting the data into json format
            
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
            //System.out.println(jsonObject);
	
            //Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();
            
            //Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);
           
            //Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            
            //Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            
            
            //Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
	
            // Database Connection
         //   String jdbcUrl = "jdbc:oracle:thin:kajal/kajal@localhost:1521:XE";
           // String username = "System";
           // String password = "system";
//            try (Connection dbConnection = DriverManager.getConnection("jdbc:oracle:thin:kajal/kajal@localhost:1521:XE","System","system")) {
//                String insertQuery = "INSERT INTO kajal.weather(city, date, temperature, humidity, windspeed, weather) VALUES (?, ?, ?, ?, ?, ?)";
//                PreparedStatement preparedStatement = dbConnection.prepareStatement(insertQuery);
//                preparedStatement.setString(1, city);
//                preparedStatement.setString(2, date);
//                preparedStatement.setInt(3, temperatureCelsius);
//                preparedStatement.setInt(4, humidity);
//                preparedStatement.setDouble(5, windSpeed);
//                preparedStatement.setString(6, weatherCondition);
//                int rowsInserted = preparedStatement.executeUpdate();
//                if (rowsInserted > 0) {
//                    System.out.println("A new record was inserted into the Weather table.");
//                }
//            } catch (SQLException e) {
//                System.out.println("SQLException: " + e.getMessage());
//            }

            // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition); 
            request.setAttribute("humidity", humidity);    
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());
	
            connection.disconnect();
            
         // Forward the request to the weather.jsp page for rendering
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
	}
}
