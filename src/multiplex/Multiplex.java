package multiplex;

/**
 *
 * @author TFEJER
 */
public class Multiplex {

    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();
        
        new Controller(view, model);
    }
    
}
