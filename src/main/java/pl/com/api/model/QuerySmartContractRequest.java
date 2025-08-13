package pl.com.api.model;

import lombok.Data;

@Data
public class QuerySmartContractRequest {
    private int contractIndex;
    private int inputType;
    private int inputSize;
    private String requestData;

    public QuerySmartContractRequest(int contractIndex, int inputType, int inputSize, String requestData) {
        this.contractIndex = contractIndex;
        this.inputType = inputType;
        this.inputSize = inputSize;
        this.requestData = requestData;
    }

    public int getContractIndex() {
        return contractIndex;
    }

    public void setContractIndex(int contractIndex) {
        this.contractIndex = contractIndex;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public int getInputSize() {
        return inputSize;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
}
