package pl.com.api.dto;

public class RpcResponse<T> {
    private String jsonrpc;
    private int id;
    private T result;

    // gettery i settery
    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public T getResult() { return result; }
    public void setResult(T result) { this.result = result; }
}
