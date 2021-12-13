public class Main {

    public static void main(String[] arg){
        GUI gui = new GUI("Group 73 Video Game DB Interface");
        SQL sql = new SQL();
        gui.initialize(sql);
        gui.run();
    }
}
