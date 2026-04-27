import java.sql.*;
import java.util.Scanner;
public class AtmSimulation {
	public static void main(String[] args) throws Exception{
		Scanner s=new Scanner(System.in);
		
		//Data Base connection
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM","root","132614");
		
		//Customer ID validation
		AtmReader atm=new AtmReader();
		System.out.println("$---------Welcome to InfinityMoney ATM----------$");
		System.out.print("Enter the ATM ID: ");
		String atmID=s.next().toLowerCase();
		atm.atmSim(atmID);
		System.out.print("\nEnter your customer ID: ");
		int customerID=s.nextInt();
		PreparedStatement pst=con.prepareStatement("SELECT name FROM account_holder where c_id=? ");
		pst.setInt(1,customerID);
		
		ResultSet rst=pst.executeQuery();
		if(rst.next()) {
			System.out.println("<<Welcome "+rst.getString("name")+">>");
		}
		else {
			System.out.println("Invalid customer ID :(");
			System.out.print("\n$--------------Transaction failed-------------$");
			return;
		}
		
		
		//PIN validation
		System.out.print("\nEnter your PIN: ");
		String pin=s.next();
		
		if(pin.length()==4){
			PreparedStatement ps=con.prepareStatement("SELECT password FROM account_holder WHERE c_id=?");
			ps.setInt(1,customerID);
			ResultSet rs=ps.executeQuery();
			
			String correctPIN=null;
			if(rs.next()) {
				correctPIN=rs.getString("password");
			}
			
			if(!correctPIN.equals(pin)){
				System.out.println("Invalid PIN :(");
				
				System.out.print("\n$--------------Transaction failed-------------$");
				return;
			}
		}
		else {
			System.out.println("Invalid PIN :(");
			System.out.print("\n$--------------Transaction failed-------------$");
			return;
		}
		
		//ATM operations
		System.out.println("1.Balance enquiry  2.Withdraw  3.Deposit  4.Cancel");
		System.out.print("Enter your choice: ");
		int ch=s.nextInt();

		while(ch<1 || ch>4) {
			System.out.println("\nInvalid choice :(");
			System.out.println("1.Balance enquiry  2.Withdraw  3.Deposit  4.Cancel");
			System.out.print("Enter Your choice: ");
			ch=s.nextInt();
		}
		
		switch(ch) {
		
		case 1:
				System.out.println("\nAvailable Balance: "+balance(con,customerID));
				break;
		
		case 2:
			double atmBalance=atm.atmReserve(atmID);
			double currentBalance=balance(con,customerID);
			System.out.print("Enter the amount: ");
			double withdrawAmount=s.nextDouble();
			while(withdrawAmount%100!=0 || withdrawAmount==0 ) {
				System.out.println("\nInvalid amount! Only 100, 200, 500 notes are available");
				System.out.print("Enter the amount: ");
				withdrawAmount=s.nextDouble();
			}
			
			if(withdrawAmount<=currentBalance) {
				if(atmBalance>=withdrawAmount) {
					System.out.println("\n<<Cash withdrawan successfully>>");
					System.out.println("Withdrawn Amount:"+withdrawAmount);
					System.out.println("Available Balance:"+withdraw(currentBalance,withdrawAmount,con,customerID));
					atm.afterWithdraw(atmID,withdrawAmount,atmBalance);
					break;				
				}
				else {
					System.out.println("\nTemporarily unable to dispense requested amount. Please try a lower amount");
					break;
				}
				
			}
			else {
				System.out.println("\n<<Insufficient account balance>>");
				break;
			}
			
			
		case 3:
			atmBalance=atm.atmReserve(atmID);
			currentBalance=balance(con,customerID);
			System.out.print("Insert the Amount: ");
			int depositAmount=s.nextInt();
			while(depositAmount%100!=0|| depositAmount==0){
				System.out.println("\nInvalid Amount! Only 100, 200, 500 Notes are Allowed");
				System.out.print("Insert the Amount: ");
				depositAmount=s.nextInt();
			}
			
			System.out.println("\n<<Cash Deposited Successfully>>");
			System.out.println("Deposited Amount:"+depositAmount);
			System.out.println("Available balance:"+deposit(currentBalance,depositAmount,con,customerID));
			atm.afterDeposit(atmID,depositAmount,atmBalance);
			break;
			
		case 4:
			System.out.println("\n<<Transaction cancelled successfully>>");
			break;
			
		default:
			System.out.println("Invalid choice :(");
			break;
		}
		
		System.out.println("\n$----------Thanks for banking with us----------$");
		System.out.print("\n               Have a nice day :)               ");
		con.close();
	}
	
	static double balance(Connection con,int customerID) throws Exception {
		
		PreparedStatement ps=con.prepareStatement("select balance from account_holder where c_id=?");
		ps.setInt(1,customerID);
		ResultSet rs=ps.executeQuery();
		
		while(rs.next())
			return rs.getDouble("balance");
		
		return -1;	
	}
	
	static double withdraw(double currentBalance,double withdrawAmount,Connection con,int customerID) throws Exception{
		
		double afterWithdraw=currentBalance-withdrawAmount;
		PreparedStatement ps=con.prepareStatement("update account_holder set balance=? where c_id=?");
		ps.setDouble(1,afterWithdraw);
		ps.setInt(2,customerID);
		ps.executeUpdate();
		
		return afterWithdraw;	
	}
	
	static double deposit(double currentBalance,int depositAmount,Connection con,int customerID )throws Exception{
		
		double afterDeposit=currentBalance+depositAmount;
		PreparedStatement ps=con.prepareStatement("UPDATE account_holder SET balance=? WHERE c_id=?");
		ps.setDouble(1,afterDeposit);
		ps.setInt(2,customerID);
		ps.executeUpdate();
		
		return afterDeposit;
	}
}