package request;

/**
 * Request Body Class for Fill requests
 */
public class FillReq {
    private String userName;
    private int generations;

    /**
     * parameterized constructor
     *
     * @param userName associated username of fill request
     * @param generations number of generations to fill
     */
    public FillReq(String userName, int generations) {
        this.userName = userName;
        this.generations = generations;
    }

    /**
     * parameterized constructor: default generations = 4
     *
     * @param userName associated username of fill request
     */
    public FillReq(String userName) {
        this.userName = userName;
        this.generations = 4;
    }

    public String getUserName() { return userName; }
    public int getGenerations() { return generations; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setGenerations(int generations) { this.generations = generations; }
}
