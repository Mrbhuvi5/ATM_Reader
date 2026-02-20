import java.util.*;
import java.sql.*;
public class ATM_Reader{
	public static void main(String[] args) throws Exception{
		Scanner s=new Scanner(System.in);
		 //DB Connection
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/atm","root","132614");
		//User Choice
		System.out.println("$-------Find the ATM's cash availability status-------$\n");
		System.out.println("1. Find ATM ID \n2. Enter ATM ID");
		int userChoice;
		System.out.print("Enter your choice: ");
		
		while (true) {
		    try {
		        userChoice = s.nextInt();
		        s.nextLine();
		        break;
		    }
		    catch (InputMismatchException e) {
		        System.out.print("\nPlease enter a valid choice: ");
		        s.nextLine();
		    }
		}
		
		if(userChoice>2) {
			System.out.println("\nInvalid choice :(");
			System.out.print("Retry");
			return;
		}
		//Find ATM Locations
		if(userChoice==1) {
			System.out.print("\nEnter  a location: ");
			String location=s.nextLine();
		
			System.out.println("Available ATMs \n1. IOB - Indian Oversease Bank "
					+ "\n2. ICIC - Industrial Credit and Investment Corporation of India"+
					"\n3. cnrb - Canara Bank"+"\n4. SBI - State Bank of India");
			int choice=0;
			System.out.print("Enter your choice: ");
			
			while(true) {
				try {
					choice=s.nextInt();
					s.nextLine();
					break;
				}
				catch (InputMismatchException e) {
			        System.out.print("\nPlease enter a valid choice: ");
			        s.nextLine();
			    }
			}
			
			if(choice<1 || choice>4) {
				System.out.println("\nInvalid Choice :(");
				System.out.print("Retry");
				return;
			}
			atmByLocation(s,con,choice,location);
		}
		//Find Avl_balance
		else if(userChoice==2) {
			System.out.print("Enter the AMT ID: ");
			String atmID=s.next().toLowerCase();
			System.out.println();
			atmIdValidation(s,con,atmID);
		}
		
		con.close();
	}
	
	static void atmByLocation(Scanner s,Connection con,int choice,String location) throws Exception{
		
		String query=null;
		if(choice==1)
			query="select * from iob where area like ? or city like ?  order by area asc";
		else if(choice==2)
			query="select * from icic where area like ? or city like ?  order by area asc";
		else if(choice==3)
			query="select * from cnrb where area like ? or city like ?  order by area asc";
		else
			query="select * from sbi where area like ? or city like ?  order by area asc";
		
		PreparedStatement ps=con.prepareStatement(query);
		String temp="iob";
		
		
		ps.setString(1,("%"+location+"%"));
		ps.setString(2,("%"+location+"%"));
		ResultSet rs=ps.executeQuery();
		
		if(rs.next()) {
			while(rs.next()) {
				System.out.print(("\n"+rs.getString("atm_id"))+" | "+rs.getString("branch_name")+" | "
						+rs.getString("area")+" | "+rs.getString("city")+"\n");
			}
		}
		else {
			System.out.println("\nNo ATM Available at that location!");
			return;
		}
		
		int option=0;
		System.out.println("\n1. Exit \n2. Enter ATM ID");
		System.out.print("Enter your choice: ");
		
		do{
			try {
			option=s.nextInt();
			s.nextLine();
			}
			catch (InputMismatchException e) {
//		        System.out.println("Enter a valid choice: ");
		        s.nextLine();
		    }
		
			if(option==1) {
				System.out.print("\n\n$----------Session ended successfully----------$");
				return;
			}
			else if(option==2) {
				System.out.print("\nEnter the ATM ID: ");
				String atmID=s.next().toLowerCase();
				atmIdValidation(s,con,atmID);
			}
			else {
				System.out.print("\nEnter a valid choice: ");
			}
		}while(option>2 || option<1);
		
	}
	
	static void atmIdValidation(Scanner s,Connection con,String atmID) throws Exception{
		
		String bank=null;
		String query=null;
		boolean valid;
		
		do {
			valid=true;
			if(atmID.contains("iob")) 
				bank="iob";
			else if(atmID.contains("icic"))
				bank="icic";
			else if(atmID.contains("cnrb")) 
				bank="cnrb";
			else if(atmID.contains("sbi"))
				bank="sbi";
			else {
					System.out.println("\nInvalid ATM ID :( ");
					valid=false;
					System.out.print("Enter a valid ATM ID: ");
					atmID=s.next().toLowerCase();
					System.out.println();
			}
			
			if(valid) {
				query=("SELECT atm_id FROM "+bank+" WHERE atm_id=?");
				PreparedStatement ps=con.prepareStatement(query);
				ps.setString(1,atmID);
				
				ResultSet rs=ps.executeQuery();
				
				if(rs.next()) {
					atmStatus(s,con,bank,atmID);
				}
				else {
					System.out.println("\nInvalid ATM ID :( ");
					valid=false;
					System.out.print("Enter a valid ATM ID: ");
					atmID=s.next().toLowerCase();
					System.out.println();
				}
			}
		}while(!valid);		

	}
	
	static void atmStatus(Scanner s,Connection con,String bank,String atmID) throws Exception{
		
		String query=("select * from "+bank+" where atm_id= ?");
		
		PreparedStatement ps=con.prepareStatement(query);
		ps.setString(1,atmID);
		
		ResultSet rs=ps.executeQuery();
		
		if(rs.next()) {
			System.out.print(rs.getString("atm_id")+" | "+rs.getString("branch_name")+" | "
					+rs.getString("area")+" | "+rs.getString("city")+"\n");
			if(rs.getDouble("avl_balance")>100000) {
				System.out.println("Available balance: 100000+");
				System.out.print("\n$----------Session ended successfully----------$");
			}
			else {
				System.out.println("Available balance: "+rs.getDouble("avl_balance"));
				System.out.print("\n$----------Session ended successfully----------$");
			}
		}
	}

}