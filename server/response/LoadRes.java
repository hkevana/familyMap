package response;


/**
 * Response Body Class for Load requests
 */
public class LoadRes {
    private String message;
    private boolean success;

    /**
     * parameterized constructor
     *
     * @param message response message
     * @param success request success
     */
    public LoadRes(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }

    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadRes loadRes = (LoadRes) o;
        return success == loadRes.success && message.equals(loadRes.message);
    }

    @Override
    public String toString() {
        return "{ " +
                "message='" + message + '\'' +
                ", success=" + success +
                " }";
    }
}
