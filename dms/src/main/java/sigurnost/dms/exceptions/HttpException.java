package sigurnost.dms.exceptions;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {

    private HttpStatus status;
    private Object data;

    public HttpException() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.data = null;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HttpException(Object data) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, data);
    }

    public HttpException(HttpStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpException{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}