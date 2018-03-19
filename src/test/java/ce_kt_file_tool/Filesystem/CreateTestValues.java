package ce_kt_file_tool.Filesystem;

import ce_kt_file_tool.entity.File;
import ce_kt_file_tool.entity.User;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

public class CreateTestValues {

    public static final String REST_SERVICE_URI = "http://localhost:8080";
    public static Boolean isCreated = false;

    public CreateTestValues(){
        if (getUser(1)== null){
            this.isCreated = true;
            createUsers();
        } else {
            System.out.println("\nTemplate Data are available!!!\n");
            listAllUsers();
        }
    }

    /* GET */
    public static User getUser(long userId){
        RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject(REST_SERVICE_URI+"/roles/"+userId, User.class);
        return user;
    }

    /* GET */
    @SuppressWarnings("unchecked")
    private void listAllUsers(){
        System.out.println("\nTesting listAllUsers API-----------\n");

        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap<String, Object>> usersMap = restTemplate.getForObject(REST_SERVICE_URI+"/users/", List.class);

        if(usersMap!=null){
            for(LinkedHashMap<String, Object> map : usersMap){
                System.out.println("User : id="+map.get("id")+", Name="+map.get("name")+", Username="+map.get("username")+", password="+map.get("password"));;
            }
        }else{
            System.out.println("No user exist----------");
        }
    }


    /* POST */
    public void createUsers() {
        User user;
        String result;

        System.out.println("Testing create Role API----------");
        RestTemplate restTemplate = new RestTemplate();

        user = new User("Daniel", "stöllner", "1356");
        result = restTemplate.postForObject(REST_SERVICE_URI+"/users/", user, String.class) ;
        System.out.println("\n User : "+result + "\n");
    }

    public static void createDrawing(File drawing) {
        String result;

        System.out.println("Testing create File API----------");
        RestTemplate restTemplate = new RestTemplate();
        //File newDrawing = drawing;
        //newDrawing.setDate(LocalDateTime.of(2014, Month.JANUARY, 1, 10, 10, 30));

        //System.out.println(drawing);
        result = restTemplate.postForObject(REST_SERVICE_URI+"/files/", drawing, String.class) ;
        //System.out.println("\n File : "+result + "\n");
        System.out.println("Location : " + result);
    }
}
