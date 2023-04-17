package com.nithu;

//200435P

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;



class EmailClient {
    private String name;
    private static int RecipientsCount=0;
    private Map<String, ArrayList<Iwishable>> wishableObj= new HashMap<String, ArrayList<Iwishable>>();


    //get today's date.
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
    String date =formatter.format(new Date());

    //check whether today birthday wishes already send or not.
    public boolean BdywishAlreadySend() throws IOException {
        String currDate ;
        File file = new File("lastRun.txt");
        if (file.exists()) {
            Scanner Reader = new Scanner(file);
            currDate = Reader.nextLine();
            if (date.equals(currDate)) {
                System.out.println("Today BDYwishes Already sent for the people in clientlist");
                return true;
            }
            else{
                FileWriter myWriter = new FileWriter("lastRun.txt");
                myWriter.write(date);
                myWriter.close();
                return false;
            }
        }
        else{
            FileWriter myWriter = new FileWriter("lastRun.txt");
            myWriter.write(date);
            myWriter.close();
            return false;
        }

    }


    //create obj and send birthday wishes if it did not send yet.
    public void Readfile(String filename) throws IOException {

        File Obj = new File(filename);
        if(Obj.exists()){
            boolean BdywishAlreadySend=BdywishAlreadySend();
            Scanner Reader = new Scanner(Obj);

            while (Reader.hasNextLine()) {
                String data = Reader.nextLine();
                RecipientsCount++;
                String[] ss = data.split(",");
                name = ss[0];
                CreateRecipientobjAndBDYWish(ss,BdywishAlreadySend);
        }}
        else{
            Obj.createNewFile();
        }
    }
    public void Readline(String line){
        RecipientsCount++;
        String[] ss = line.split(",");
        name = ss[0];
        CreateRecipientobjAndBDYWish(ss,false);
    }
    public  void CreateRecipientobjAndBDYWish(String[] ss,boolean BdywishAlreadySend){
            if (ss.length == 3) {
                OfficialRecipient name = new OfficialRecipient(ss[0], ss[1], ss[2]);
            }

            else if (ss[1].indexOf("@") == -1) {
                PersonalRecipient name = new PersonalRecipient(ss[0], ss[1], ss[2], ss[3]);

                if(wishableObj.containsKey(ss[3].substring(5))){
                    wishableObj.get(ss[3].substring(5)).add(name);
                }
                else{
                    wishableObj.put(ss[3].substring(5),new ArrayList<>());
                    wishableObj.get(ss[3].substring(5)).add(name);
                }
                if(date.equals(ss[3].substring(5)) && !BdywishAlreadySend){
                    name.BDYwish(ss[2]);
                }
            }

            else {
                OfficeFriendRecipient name = new OfficeFriendRecipient(ss[0], ss[1], ss[2], ss[3]);
                if(wishableObj.containsKey(ss[3].substring(5))){
                    wishableObj.get(ss[3].substring(5)).add(name);
                }
                else{
                    wishableObj.put(ss[3].substring(5),new ArrayList<>());
                    wishableObj.get(ss[3].substring(5)).add(name);
                }
                if(date.equals(ss[3].substring(5)) && !BdywishAlreadySend){
                    name.BDYwish(ss[1]);
                }
            }
        }



    //add new recipient detail in the emailclient file.
    public void AddNewRecipient(String RecipientDetails) throws IOException {
        FileWriter fstream = new FileWriter("clientList.txt",true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("\n"+RecipientDetails);
        out.close();
    }


    //get birthday persons on a specific day
    public ArrayList<String> getBirthdayPersons(String date) {
        ArrayList<String> list=new ArrayList<>();
        if(wishableObj.containsKey(date)){
        int size=wishableObj.get(date).size();

        for(int i=0;i<size;i++){
            list.add(wishableObj.get(date).get(i).getname());
        }}

        return list;

    }

    public static int getRecipientsCount() {
        return RecipientsCount;
    }
}



interface Iwishable {
    void BDYwish(String email);
    String getname();
}


abstract class Recipient {
    private String name;
    private String email;

    public Recipient(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }
}


class PersonalRecipient extends Recipient implements Iwishable{
    private String nickName;
    private Date bod;



    public PersonalRecipient(String name, String email, String nickName, String bod) {
        super(name, email);
        this.nickName = nickName;
        this.bod = new Date(bod);
    }



    @Override
    public void BDYwish(String mail) {

        String content="Hi "+getname()+"\n Happy Happy Birthday My Dear Sweet Friend. I hope you have a great day.\n" +
                "Take care,\n" +
                " Nithu";
        SendEmail sendEmail=new SendEmail();
        sendEmail.SendMaill(mail,"BDY_wish",content);}




    @Override
    public String getname() {
        return super.getName();
    }
}



class OfficeFriendRecipient extends Recipient implements Iwishable{
    private String designation;
    private Date bod;


    public OfficeFriendRecipient(String name, String email, String designation, String bod) {
        super(name, email);
        this.designation = designation;
        this.bod = new Date(bod);
    }



    @Override
    public void BDYwish(String mail) {
        String content="Dear " +getname() +
                "\n" +
                "I am writing to wish you a happy birthday. I hope that you enjoy the day.\n" +
                "\n" +
                "Many happy returns!\n" +
                "\n" +
                "Kind regards,\n" +
                "\n" +
                "K.Nithursika";
        SendEmail sendEmail=new SendEmail();
        sendEmail.SendMaill(mail,"BDYwish",content);}



    @Override
    public String getname() {
        return super.getName();
    }
}


class OfficialRecipient extends Recipient{
    private String designation;

    public OfficialRecipient(String name, String email, String designation) {
        super(name, email);
        this.designation = designation;
    }
}


class SendEmail {
    private static String username;
    private static String password;
    private static ArrayList<SentEmailDetails> detail=new ArrayList<SentEmailDetails>();



    SimpleDateFormat formatter = new SimpleDateFormat("YYYY/MM/dd");
    String Date=formatter.format(new Date());

    public void SendMaill(String email, String subject, String content)  {


        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject(subject);
            message.setText(content);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        try {
            Transport.send(message);
            System.out.println("A email sent to "+ email + " on:"+ subject +" subject");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            SentEmailDetails sentEmailDetails=new SentEmailDetails(Date,email,subject,content);
            detail.add(sentEmailDetails);
            sentEmailDetails.adddetails(detail);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<SentEmailDetails> getDetail() {
        return detail;
    }

    public static void setUsername(String username) {
        SendEmail.username = username;
    }

    public static void setPassword(String password) {
        SendEmail.password = password;
    }
}



class SentEmailDetails implements Serializable {
    String content;
    String email;
    String subject;
    String date;

    public SentEmailDetails(  String date,String email, String subject,String content) {
        this.content=content;
        this.email = email;
        this.subject = subject;
        this.date = date;
    }

    public void adddetails(ArrayList<SentEmailDetails> detail) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("output.txt"));
        os.writeObject(detail);
        os.close();

    }
    public String getSubject() {
        return subject;
    }

    public String getEmail() {
        return email;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}





public class Email_Client {

    public static void main(String[] args) throws IOException {
          Scanner scanner = new Scanner(System.in);

        //get user email address and password.
        //System.out.print("Enter your email address : ");
//        String emailAddress= scanner.nextLine();
//        SendEmail.setUsername(emailAddress);
//        System.out.print("Enter your email password : ");
//        String password = scanner.nextLine();
        String emailAddress="xxxxxxx@gmail.com";
        SendEmail.setUsername(emailAddress);
        String password ="xxxxxxxxxxx";
        SendEmail.setPassword(password);


        //create recipient object and send wishes to today's birthday persons.
        EmailClient emailClient=new EmailClient();
        emailClient.Readfile("clientList.txt");

        boolean quit=false;
        while(!quit){
            System.out.println("Enter option type: \n"
                    + "1 - Adding a new recipient\n"
                    + "2 - Sending an email\n"
                    + "3 - Printing out all the recipients who have birthdays\n"
                    + "4 - Printing out details of all the emails sent\n"
                    + "5 - Printing out the number of recipient objects in the application");

            int option = scanner.nextInt();

            Scanner scanner1 = new Scanner(System.in);


            switch(option) {
                case 1:
                    //Adding a new recipient

                    System.out.print("Enter RecipientDetails as :" +
                            " Personal: sunil,<nick-name>,sunil@gmail.com,2000/10/10\n" +
                            "  Office_friend: kamal,kamal@gmail.com,clerk,2000/12/12\n" +
                            " Official: nimal , nimal@gmail.com, ceo ");
                    String RecipientDetails = scanner1.nextLine();
                    emailClient.AddNewRecipient(RecipientDetails);
                    emailClient.Readline(RecipientDetails);

                    break;

                case 2:
                    //Sending an email

                    System.out.print("Enter email address : ");
                    String email = scanner1.nextLine();
                    System.out.print("subject : ");
                    String subject = scanner1.nextLine();
                    System.out.print("content : ");
                    String content = scanner1.nextLine();
                    SendEmail sendEmail = new SendEmail();
                    sendEmail.SendMaill(email, subject, content);
                    break;

                case 3:
                    //Printing out all the recipients who have birthdays

                    System.out.print("Enter date : (input format - yyyy/MM/dd)");
                    String date = scanner1.nextLine();
                    ArrayList<String> ll = emailClient.getBirthdayPersons(date.substring(5));
                    int count = ll.size();
                    System.out.println("No of BDY members :"+count);
                    for (int i = 0; i < count; i++) {
                        System.out.println(ll.get(i));
                    }
                    break;

                case 4:
                    //Printing out details of all the emails sent

                    System.out.println("input format - yyyy/MM/dd (ex: 2018/09/17)");
                    String data4 = scanner1.nextLine();
                    ArrayList<SentEmailDetails> detail = SendEmail.getDetail();

                    try {
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream("output.txt"));
                        detail = (ArrayList<SentEmailDetails>) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    boolean b2 = false;

                    for (int k = 0; k < detail.size(); k++) {
                        SentEmailDetails d = detail.get(k);
                        if (d.getDate().equals(data4)) {
                            b2 = true;
                            System.out.println("Recipient's Email Address : " + d.getEmail()
                                    + ", \t Subject : " + d.getSubject() + "\n" + ", \t condent : " + d.getContent());
                        }
                    }
                    if (!b2) {
                        System.out.println("No emails on that date");
                    }
                    break;

                case 5:
                    //Printing out the number of recipient objects in the application

                    int Count = EmailClient.getRecipientsCount();
                    System.out.println(Count);
                    break;

                default: quit=true; break;
            }
        }

}}
