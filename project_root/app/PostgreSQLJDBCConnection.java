import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;
import java.time.LocalDate;

public class PostgreSQLJDBCConnection {

    private static Connection conn = null;
    
    //replace these with your own database credentials
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "M1ck@y";
    public static void main(String[] args){

        try{
            Class.forName("org.postgresql.Driver");
            //opens a connection to the database 
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            if(conn != null){
                System.out.println("Connected to PostgreSQL successfully!");
                mainMenu();
            }
            else{
                System.out.println("Failed to establish connection.");
                return;
            }
        }
        //if there is any error while connecting to database will be printed here
        catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        finally{
            //closing connection when finished
            if(conn != null){
                try{
                    conn.close();
                }
                catch(SQLException ignore){
                
                }
            }
        }
    }

    //function that presents the default menu to user and options for the user to choose from
    private static void mainMenu(){
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("Welcome to the Fitness Club");
            System.out.println("1. Member Menu");
            System.out.println("2. Trainer Menu");
            System.out.println("3. Admin Menu");
            System.out.println("0. Exit");
            System.out.print("Please enter an option: ");

            String choice = scanner.nextLine();

            //calling function based on user's choice
            switch(choice){
                case "1":
                    memberMenu(scanner);
                    break;
                case "2":
                    trainerMenu(scanner);
                    break;
                case "3":
                    adminMenu(scanner);
                    break;
                case "0":
                    System.out.println("Bye!");
                    return;
                default:
                    System.out.println("Error: Invalid choice.");
                    
            }
        }
    }

    //function that presents the member menu to user and options for the user to choose from
    private static void memberMenu(Scanner scanner){
         while(true){
             //options shown to user
            System.out.println("Member Menu");
            System.out.println("1. Register new member");
            System.out.println("2. Update profile");
            System.out.println("3. Add health metric");
            System.out.println("4. View dashboard");
            System.out.println("0. Back");
            System.out.print("Please enter an option: ");

            String choice = scanner.nextLine();

            //calling function based on user's choice
            switch(choice){
                case "1":
                    registerMember(scanner);
                    break;
                case "2":
                    updateMemberProfile(scanner);
                    break;
                case "3":
                    addHealthMetric(scanner);
                    break;
                case "4":
                    viewMemberDashboard(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Error: Invalid choice.");
                    
            }
        }

    }

    //function that registers a new member to database
    private static void registerMember(Scanner scanner){
        //asking user to insert information that will be placed in the database
        System.out.println("Please insert your information below: ");
        System.out.println("Name: ");
        String name = scanner.nextLine();

        //error checking: must require at least a name when adding a new member
        if(name.isEmpty()){
            System.out.println("Name is required. Member was not added.");
            return;
        }

        //gathering input from user for each attribute
        System.out.println("Date of birth (YYYY-MM-DD): ");
        String birthDate = scanner.nextLine();

        System.out.println("Gender: ");
        String gender = scanner.nextLine();

        System.out.println("Phone number: ");
        String phoneNumber = scanner.nextLine();

        System.out.println("Email address: ");
        String email = scanner.nextLine();

        System.out.println("Target weight (kg): ");
        String targetWeight = scanner.nextLine();
        
        String addSql = "INSERT INTO members (member_name, date_of_birth, gender, phone_number, email_address, target_weight) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

        //adding a new row with user's input to the SQL database
        try(PreparedStatement ps = conn.prepareStatement(addSql)){
            //only required value (primary key)
            ps.setString(1, name);

            //rest of the values can be null or "unknown"
            if(birthDate.isEmpty()){
                ps.setDate(2, null);
            }
            else{
                ps.setDate(2, Date.valueOf(birthDate));
            }

            if(gender.isEmpty()){
                ps.setString(3, "Unknown");
            }
            else{
                ps.setString(3, gender);
            }

            if(phoneNumber.isEmpty()){
                ps.setString(4, "Unknown");
            }
            else{
                ps.setString(4, phoneNumber);
            }

            if(email.isEmpty()){
                ps.setString(5, null);
            }
            else{
                ps.setString(5, email);
            }

            if(targetWeight.isEmpty()){
                ps.setDouble(6, 0.0);
            }
            else{
                ps.setDouble(6, Double.parseDouble(targetWeight));
            }

            //updating the database with new row
            int rows = ps.executeUpdate();
            //making sure row was added, else will print "unable to add member."
            if(rows > 0){
                System.out.println("Member has been added successfully.");
            }
            else{
                System.out.println("Unable to add member.");
            }
        }
        //error handling
        catch(SQLException e){
            System.out.println("Error registering member: " + e.getMessage());
        }
        catch(IllegalArgumentException e){
            System.out.println("Invalid date or number.");
        }
    }

    //function that updates a current member's information
    private static void updateMemberProfile(Scanner scanner){
        System.out.println("Please enter your member ID: ");
        System.out.println("Member ID: ");
        int memberId;
        
        //recording user input
        try{
            memberId = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid member ID.");
            return;
        }

        String selectSQL = "SELECT phone_number, email_address, target_weight " +
        "FROM members WHERE member_id = ?";

        //adding a new row with user's input to the SQL database
        try(PreparedStatement selectPs = conn.prepareStatement(selectSQL)){
            selectPs.setInt(1, memberId);

            String currentPhoneNumber = null;
            String currentEmail = null;
            double currentTargetWeight = 0.0;

            try(ResultSet rs = selectPs.executeQuery()){
                if(!rs.next()){
                    System.out.println("There is no member with that ID.");
                    return;
                }
                //getting user's present info from database
                currentPhoneNumber = rs.getString("phone_number");
                currentEmail = rs.getString("email_address");
                currentTargetWeight = rs.getDouble("target_weight");
            }

            //current values
            System.out.println("Current phone number: " + currentPhoneNumber);
            System.out.println("Current email: " + currentEmail);
            System.out.println("Current target weight: " + currentTargetWeight);

            //asking user for updated values
            System.out.print("Please enter your new phone number: ");
                String updatedPhoneNumber = scanner.nextLine();
                if(updatedPhoneNumber.isEmpty()){
                    updatedPhoneNumber = currentPhoneNumber;
                }

            System.out.print("Please enter your new email address: ");
            String updatedEmail = scanner.nextLine();
            if(updatedEmail.isEmpty()){
                updatedEmail = currentEmail;
            }

            System.out.print("Please enter your new target weight: ");
            String updatedTargetWeight = scanner.nextLine();
            double newTarget;
            if(updatedTargetWeight.isEmpty()){
                newTarget = currentTargetWeight;
            }
            else{
                newTarget = Double.parseDouble(updatedTargetWeight);
            }

            String updateSQL = "UPDATE members " + "SET phone_number = ?, email_address = ?, target_weight = ? " +
            "WHERE member_id = ?";

            //updating the selected row with a new information
            try(PreparedStatement updatePs = conn.prepareStatement(updateSQL)){
                updatePs.setString(1, updatedPhoneNumber);
                updatePs.setString(2, updatedEmail);
                updatePs.setDouble(3, newTarget);
                updatePs.setInt(4, memberId);

                int rows = updatePs.executeUpdate();
                //making sure row was added, if not print, "Profile has not been updated."
                if(rows > 0){
                    System.out.println("Profile has been updated.");
                }
                else{
                    System.out.println("Profile has not been updated.");
                }
            }
        }
        //error handling
        catch(SQLException e){
            System.out.println("There was an error updating the profile: " + e.getMessage());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid target weight.");
            return;
        }
    }

    //function that adds health metric table that points to a member ID
    private static void addHealthMetric(Scanner scanner){
        System.out.println("Please enter your member ID.");
        System.out.print("Member ID: ");
        int memberId;
        
        //error checking
        try{
            memberId = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid member ID.");
            return;
        }

        //asking user for information
        System.out.println("Please enter your information below: ");
        System.out.print("Weight (kg): ");
        String weight = scanner.nextLine();

        System.out.print("Heart rate (bpm): ");
        String heartRate = scanner.nextLine();

        System.out.print("Height (cm): ");
        String height = scanner.nextLine();

        //recording Date based on what date is it currently 
        Date recordAt = Date.valueOf(LocalDate.now());

        String sql = "INSERT INTO health_metric(member_id, weight_kg, heart_rate, height, recorded_at) " +
        "VALUES (?, ?, ?, ?, ?)";

        //inserting new row and information into the database table
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, memberId);
            ps.setDouble(2, Double.parseDouble(weight));
            ps.setInt(3, Integer.parseInt(heartRate));
            ps.setDouble(4, Double.parseDouble(height));
            ps.setDate(5, recordAt);

            int rows = ps.executeUpdate();
            //making sure row was added
            if(rows > 0){
                    System.out.println("Metric has been recorded.");
            }
            else{
                System.out.println("Unable to add metric.");
            }   
        }
        //error handling
        catch(SQLException e){
            System.out.println("There was an error while inserting metric: " + e.getMessage());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid number format.");
        }
    }

    //function that allows member to view their dashboard
    private static void viewMemberDashboard(Scanner scanner){
        System.out.println("Member Dashboard");
        System.out.println("Please enter your Member ID: ");
        int memberId;

        //error checking
        try{
            memberId = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid member ID.");
            return;
        }
        //getting latest health metric for member from database
        String metricSql = "SELECT m.member_name, m.target_weight, " +
        "       hm.weight_kg, hm.heart_rate, hm.height, hm.recorded_at " +
        "FROM members m " + 
        "LEFT JOIN health_metric hm ON hm.member_id = m.member_id " +
        "WHERE m.member_id = ? " +
        "ORDER BY hm.recorded_at DESC " +
        "LIMIT 1";

        //getting all PT sessions for this member from database
        String sessionsSql = "SELECT ps.session_id, t.trainer_name, r.room_name, " +
        "       ps.start_time, ps.end_time, ps.session_status " +
        "FROM pt_session ps " + 
        "JOIN trainers t ON ps.trainer_id = t.trainer_id " +
        "JOIN room r ON ps.room_id = r.room_id " +
        "WHERE ps.member_id = ? " +
        "ORDER BY ps.start_time";

        try{
            try(PreparedStatement ps = conn.prepareStatement(metricSql)){
                ps.setInt(1, memberId);

                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        //printing user's health metrics and personal information
                        System.out.println("Member: " + rs.getString("member_name"));
                        System.out.println("Target weight: " + rs.getDouble("target_weight"));
                        System.out.println("Last recorded weight: " + rs.getDouble("weight_kg"));
                        System.out.println("Last heart rate: " + rs.getInt("heart_rate"));
                        System.out.println("Last height: " + rs.getDouble("height"));
                        System.out.println("Recorded at: " + rs.getDate("recorded_at"));
                    }
                    else{
                        System.out.println("No member or no metrics were found for this ID.");
                        return;
                    }
                }
            }
            System.out.println("PT Sessions");
            try(PreparedStatement ps2 = conn.prepareStatement(sessionsSql)){
                ps2.setInt(1, memberId);

                try(ResultSet rs2 = ps2.executeQuery()){
                    boolean any = false;
                    while(rs2.next()){
                        any = true;
                        //printing all of user's sessions
                        System.out.printf(
                            "Session %d: Trainer= %s, Room= %s, %d-%d, Status= %s%n",
                            rs2.getInt("session_id"),
                            rs2.getString("trainer_name"),
                            rs2.getString("room_name"),
                            rs2.getInt("start_time"),
                            rs2.getInt("end_time"),
                            rs2.getString("session_status")
                        );
                    }
                    if(!any){
                        System.out.println("No sessions were found for this member.");
                    }
                }
            }
        }
        //error checking
        catch(SQLException e){
            System.out.println("Error loading dashboard: " + e.getMessage());
        }   
    }

    //function that presents menu for trainers to choose an option from
    private static void trainerMenu(Scanner scanner){
        while(true){
            //presenting choices to user
            System.out.println("Trainer Menu");
            System.out.println("1. Set availability");
            System.out.println("2. View my schedule");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine();

            //calling function based on user's choice
            switch(choice){
                case "1":
                    setTrainerAvailability(scanner);
                    break;
                case "2":
                    viewTrainerSchedule(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    //function that allows trainer to set their availability for sessions
    private static void setTrainerAvailability(Scanner scanner){
        System.out.println("Set Trainer Availability");
        System.out.print("Please enter your trainer ID: ");
        int trainerId;
        //getting trainer ID from user
        try{
            trainerId = Integer.parseInt(scanner.nextLine());
        }
        //error checking: checking if trainer inputs valid number
        catch(NumberFormatException e){
            System.out.println("Invalid trainer ID.");
            return;
        }

        //getting start time from user
        System.out.println("Please enter a start time: ");
        int startTime;
        try{
            startTime = Integer.parseInt(scanner.nextLine());
        }
        //error checking: checking if trainer inputs valid start time
        catch(NumberFormatException e){
            System.out.println("Invalid start time.");
            return;
        }

        //getting end time from user
        System.out.println("Please enter an end time: ");
        int endTime;
        try{
            endTime = Integer.parseInt(scanner.nextLine());
        }
        //error checking: checking if trainer inputs valid end time
        catch(NumberFormatException e){
            System.out.println("Invalid end time.");
            return;
        }

        String sql = "INSERT INTO trainer_availability(trainer_id, start_time, end_time) " +
        "VALUES (?, ?, ?)";

        //adding information to new row
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, trainerId);
            ps.setInt(2, startTime);
            ps.setInt(3, endTime);

            int rows = ps.executeUpdate();
            //checking if row was added, if not print: "There was an overlap in availability or another problem."
            if(rows > 0){
                System.out.println("Availability added.");
            }
            else{
                System.out.println("There was an overlap in availability or another problem.");
            }

        }
        //error checking
        catch(SQLException e){
            System.out.println("There was an error setting trainer availability: " + e.getMessage());
        }
    }

    //function that allows trainer to view their schedule 
    private static void viewTrainerSchedule(Scanner scanner){
        System.out.println("Trainer Schedule");
        System.out.print("Please enter your trainer ID: ");
        int trainerId;
        //getting trainer ID from user
        try{
            trainerId = Integer.parseInt(scanner.nextLine());
        }
        //error checking: checking if trainer inputs valid number
        catch(NumberFormatException e){
            System.out.println("Invalid trainer ID.");
            return;
        }

        //getting all of trainer's PT sessions
        String sql = "SELECT session_id, member_name, room_name, " +
        "       start_time, end_time, session_status " + 
        "FROM trainer_schedule " +
        "WHERE trainer_id = ? " +
        "ORDER BY start_time";

         try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1, trainerId);

                try(ResultSet rs = ps.executeQuery()){
                    boolean any = false;
                    while(rs.next()){
                        any = true;
                        //printing user's information
                        System.out.printf(
                            "Session %d: Member= %s, Room= %s, %d-%d, Status= %s%n",
                            rs.getInt("session_id"),
                            rs.getString("member_name"),
                            rs.getString("room_name"),
                            rs.getInt("start_time"),
                            rs.getInt("end_time"),
                            rs.getString("session_status")
                        );
                    }
                    if(!any){
                        System.out.println("No sessions were found for this trainer.");
                    }
                }
            }
        //error checking
        catch(SQLException e){
            System.out.println("Error loading trainer schedule: " + e.getMessage());
        }   

    }

    //function that provides a menu for administrators choices to choose from
    private static void adminMenu(Scanner scanner){
        while(true){
        //presenting options to user
        System.out.println("Admin Menu");
        System.out.println("1. Book PT session");
        System.out.println("2. View room schedule");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        //calling a certain function based on user's input
        switch(choice){
            case "1":
                bookPtSession(scanner);
                break;
            case "2":
                viewRoomSchedule(scanner);
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid choice.");
            }
        }
    }

    //function that allows administrators to book PT sessions for members and trainers
    private static void bookPtSession(Scanner scanner){
        System.out.println("Book PT Session");
        int memberId, trainerId, roomId, startTime, endTime;
        
        //asking admin to enter information in order to book a session
        try{
            System.out.println("Please enter the member's ID: ");
            memberId = Integer.parseInt(scanner.nextLine());

            System.out.println("Please enter the trainer's ID: ");
            trainerId = Integer.parseInt(scanner.nextLine());

            System.out.println("Please enter the room's ID: ");
            roomId = Integer.parseInt(scanner.nextLine());

            System.out.println("Please enter the start time: ");
            startTime = Integer.parseInt(scanner.nextLine());

            System.out.println("Please enter the end time: ");
            endTime = Integer.parseInt(scanner.nextLine());
        }
        catch(NumberFormatException e){
            System.out.println("Invalid number format.");
            return;
        }

        try{
            //creating new row in trainer availability table
            String availableSql = "SELECT 1 FROM trainer_availability " +
            "WHERE trainer_id = ? " +
            "   AND start_time <= ? " +
            "   AND end_time >= ? " +
            "LIMIT 1";

            //adding new information to row
            try(PreparedStatement ps = conn.prepareStatement(availableSql)){
                ps.setInt(1, trainerId);
                ps.setInt(2, startTime);
                ps.setInt(3, endTime);

                try(ResultSet rs = ps.executeQuery()){
                    //prints if no new row was added
                    if(!rs.next()){
                        System.out.println("Trainer is not available during that time.");
                        return;
                    }
                }
            }
            //checking room status from PT sesssion table
            String roomSql = "SELECT 1 FROM pt_session " +
            "WHERE room_id = ? " +
            "   AND session_status = 'scheduled' " +
            "   AND NOT (end_time <= ? OR start_time >= ?) " +
            "LIMIT 1";

            //adding new row to room schedule
            try(PreparedStatement ps = conn.prepareStatement(roomSql)){
                ps.setInt(1, roomId);
                ps.setInt(2, startTime);
                ps.setInt(3, endTime);
                
                try(ResultSet rs = ps.executeQuery()){
                    //checking to see if a new row was added
                    if(rs.next()){
                        System.out.println("Room is booked during this time.");
                        return;
                    }
                }
            }
            //inserting new row to PT session table
            String insertSql = "INSERT INTO pt_session(member_id, trainer_id, room_id, start_time, end_time, session_status) " + 
            "VALUES (?, ?, ?, ?, ?, 'scheduled')";

            //adding values to new row in PT session table
            try(PreparedStatement ps = conn.prepareStatement(insertSql)){
                ps.setInt(1, memberId);
                ps.setInt(2, trainerId);
                ps.setInt(3, roomId);
                ps.setInt(4, startTime);
                ps.setInt(5, endTime);

                int rows = ps.executeUpdate();
                if(rows > 0){
                    System.out.println("Session has been booked.");
                }
                else{
                    System.out.println("Session was not booked.");
                }
            } 
        }
        //error checking
        catch(SQLException e){
            System.out.println("There was an error while booking the session: " + e.getMessage());
        }
    }

    //function that allows administrators to view a room's schedule
    private static void viewRoomSchedule(Scanner scanner){
     System.out.println("Room Schedule");
        System.out.print("Please enter a room ID: ");
        int roomId;
        //getting room ID from user
        try{
            roomId = Integer.parseInt(scanner.nextLine());
        }
        //error checking: checking if admin inputs valid number
        catch(NumberFormatException e){
            System.out.println("Invalid room ID.");
            return;
        }

        //gathering information from member, pt_session and trainer tables
        String sql = "SELECT ps.session_id, m.member_name, t.trainer_name, " +
        "       ps.start_time, ps.end_time, ps.session_status " + 
        "FROM pt_session ps " +
        "JOIN members m ON ps.member_id = m.member_id " +
        "JOIN trainers t ON ps.trainer_id = t.trainer_id " +
        "WHERE ps.room_id = ? " +
        "ORDER BY ps.start_time";

         try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1, roomId);

                try(ResultSet rs = ps.executeQuery()){
                    boolean any = false;
                    while(rs.next()){
                        any = true;
                        //printing information to user
                        System.out.printf(
                            "Session %d: Member= %s, Trainer= %s, %d-%d, Status= %s%n",
                            rs.getInt("session_id"),
                            rs.getString("member_name"),
                            rs.getString("trainer_name"),
                            rs.getInt("start_time"),
                            rs.getInt("end_time"),
                            rs.getString("session_status")
                        );
                    }
                    //checking to see if row exists
                    if(!any){
                        System.out.println("No sessions are scheduled for this room.");
                    }
                }
            }
             //error handling
        catch(SQLException e){
            System.out.println("Error loading room schedule: " + e.getMessage());
        }   
    }
}
