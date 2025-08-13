package pl.com.api.model;

import lombok.Data;

@Data
public class QuerySmartContractResponse {
    private String output;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

}
