package response;

/**
 * Response Class for Clear requests
 */
public class ClearRes {
    private String message;
    private boolean success;

    /**
     * parameterized constructor
     *
     * @param message response message
     * @param success success of request
     */
    public ClearRes(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }

    public void setMessage(String errMessage) { this.message = errMessage; }
    public void setSuccess(boolean success) { this.success = success; }

    @Override
    public String toString() {
        return "{ " +
                "message='" + message + '\'' +
                ", success=" + success +
                " }";
    }
}
