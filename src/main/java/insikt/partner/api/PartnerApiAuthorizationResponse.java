package insikt.partner.api;

import java.util.List;
import java.util.Map;

public class PartnerApiAuthorizationResponse {
    private final Map apiResponse;

    public PartnerApiAuthorizationResponse(Map apiResponse) {
        this.apiResponse = apiResponse;
    }

    public boolean callSucceded() {
        return (apiResponse != null && apiResponse.containsKey("success") && apiResponse.get("success").equals(true));
    }

    public String getPaymentTransactionId() {
        Map partnerApiResponseData = (Map) apiResponse.get("data");
        return (String) partnerApiResponseData.get("paymentTransactionId");
    }

    public String getAuthorizationId() {
        Map partnerApiResponseData = (Map) apiResponse.get("data");
        List<Map> products = (List<Map>) partnerApiResponseData.get("products");
        Map product = products.get(0);
        return String.valueOf(product.get("authorizationId"));
    }
}
