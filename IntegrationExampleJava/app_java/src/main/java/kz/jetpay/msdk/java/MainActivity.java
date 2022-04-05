package kz.jetpay.msdk.java;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.wallet.PaymentDataRequest;

import kz.jetpay.msdk.*;
import kz.jetpay.msdk.threeDSecure.*;


public class MainActivity extends AppCompatActivity {

    private static int PAY_ACTIVITY_REQUEST = 888;
    private static String SECRET = "test_integration_jetpay_salt";
    private static int PROJECT_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create payment info with product information
        JetpayHostsPaymentInfo paymentInfo = getPaymentInfoOnlyRequiredParams(); // getPaymentInfoAllParams

        // Signature should be generated on your server and delivered to your app
        String signature = SignatureGenerator.generateSignature(paymentInfo.getParamsForSignature(), SECRET);

        // Sign payment info
        paymentInfo.setSignature(signature);

        // Present Checkout UI
        startActivityForResult(JetpayHostsSDK.buildIntent(
                this,
                paymentInfo),
                PAY_ACTIVITY_REQUEST);
    }

    // Handle SDK result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_ACTIVITY_REQUEST) {

            switch (resultCode) {
                case JetpayHostsSDK.RESULT_SUCCESS: {
                    break;
                }
                case JetpayHostsSDK.RESULT_CANCELLED: {
                    break;
                }
                case JetpayHostsSDK.RESULT_DECLINE: {
                    break;
                }
                case JetpayHostsSDK.RESULT_FAILED: {
                    break;
                }
                default: {
                    break;
                }
            }

            if (data != null && data.hasExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_ERROR)) {
                String error = data.getStringExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_ERROR);
            }

            if (data != null && data.hasExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_TOKEN)) {
                String token = data.getStringExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_TOKEN);
            }
            if (data != null && data.hasExtra(JetpayHostsSDK.DATA_INTENT_SESSION_INTERRUPTED)) {
                String token = data.getStringExtra(JetpayHostsSDK.DATA_INTENT_SESSION_INTERRUPTED);
            }
        }
    }

    // Payment Info
    JetpayHostsPaymentInfo getPaymentInfoOnlyRequiredParams() {
        return new JetpayHostsPaymentInfo(
                PROJECT_ID, // project ID that is assigned to you
                "test_integration_jetpay_id", // payment ID to identify payment in your system
                100, // 1.00
                "USD"
        );
    }

    JetpayHostsPaymentInfo getPaymentInfoAllParams() {
        return new JetpayHostsPaymentInfo(
                PROJECT_ID, // project ID that is assigned to you
                "test_integration_jetpay_id", // payment ID to identify payment in your system
                100, // 1.00
                "USD",
                "T-shirt with dog print",
                "10", // unique ID assigned to your customer
                ""
        );
    }

    // Additional
    void setDMSPayment(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setAction(JetpayHostsPaymentInfo.ActionType.Auth);
    }

    void setActionTokenize(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setAction(JetpayHostsPaymentInfo.ActionType.Tokenize);
    }

    void setActionVerify(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setAction(JetpayHostsPaymentInfo.ActionType.Verify);
    }

    void setToken(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setToken("token");
    }

    void setReceiptData(JetpayHostsPaymentInfo paymentInfo) {
        final String RECEIPT_DATA = "receipt data";
        paymentInfo.setReceiptData(RECEIPT_DATA);
    }

    void setRecurrent(JetpayHostsPaymentInfo paymentInfo) {
        JetpayHostsRecurrentInfo recurrentInfo = new JetpayHostsRecurrentInfo(
                "R", // type
                "20", // expiry day
                "11", // expiry month
                "2030", // expiry year
                "M", // recurrent period
                "12:00:00", // start time
                "12-02-2020", // start date
                "your_recurrent_id"); // recurrent payment ID

        // Additional options if needed
        recurrentInfo.setAmount(1000);
        recurrentInfo.setSchedule(new JetpayHostsRecurrentInfoSchedule[]{
                new JetpayHostsRecurrentInfoSchedule("20-10-2020", 1000),
                new JetpayHostsRecurrentInfoSchedule("20-10-2020", 1000)
        });

        paymentInfo.setRecurrent(recurrentInfo);
    }

    void setKnownAdditionalFields(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setJetpayHostsAdditionalFields(new JetpayHostsAdditionalField[]{
                new JetpayHostsAdditionalField(JetpayHostsAdditionalFieldEnums.AdditionalFieldType.customer_first_name, "Mark"),
                new JetpayHostsAdditionalField(JetpayHostsAdditionalFieldEnums.AdditionalFieldType.billing_country, "US"),
        });
    }

    // if you want to hide the saved cards, pass the value - true
    void setHideSavedWallets(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setHideSavedWallets(false);
    }

    // For forced opening of the payment method, pass its code. Example: qiwi, card ...
    void setForcePaymentMethod(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setForcePaymentMethod("card");
    }

    // Setup 3D Secure 2.0 parameters
    void setThreeDSecureParams(JetpayHostsPaymentInfo paymentInfo) {
        JetpayHostsThreeDSecureInfo threeDSecureInfo = new JetpayHostsThreeDSecureInfo();

        JetpayHostsThreeDSecurePaymentInfo threeDSecurePaymentInfo = new JetpayHostsThreeDSecurePaymentInfo();

        threeDSecurePaymentInfo
                .setReorder("01") // This parameter indicates whether the cardholder is reordering previously purchased merchandise.
                .setPreorderPurchase("01") // This parameter indicates whether cardholder is placing an order for merchandise with a future availability or release date.
                .setPreorderDate("01-10-2019") // The date the preordered merchandise will be available.Format: dd-mm-yyyy.
                .setChallengeIndicator("01") // This parameter indicates whether challenge flow is requested for this payment.
                .setChallengeWindow("01"); // The dimensions of a window in which authentication page opens.

        JetpayHostsThreeDSecureGiftCardInfo threeDSecureGiftCardInfo = new JetpayHostsThreeDSecureGiftCardInfo();

        threeDSecureGiftCardInfo
                .setAmount(12345) // Amount of payment with prepaid or gift card denominated in minor currency units.
                .setCurrency("USD") // Currency of payment with prepaid or gift card in the ISO 4217 alpha-3 format
                .setCount(1); // Total number of individual prepaid or gift cards/codes used in purchase.

        threeDSecurePaymentInfo.setGiftCard(threeDSecureGiftCardInfo);

        JetpayHostsThreeDSecureCustomerInfo threeDSecureCustomerInfo = new JetpayHostsThreeDSecureCustomerInfo();
        threeDSecureCustomerInfo
                .setAddressMatch("Y") //The parameter indicates whether the customer billing address matches the address specified in the shipping object.
                .setHomePhone("79105211111") // Customer home phone number.
                .setWorkPhone("73141211111") // Customer work phone number.
                .setBillingRegionCode("ABC"); // State, province, or region code in the ISO 3166-2 format. Example: SPE for Saint Petersburg, Russia.

        JetpayHostsThreeDSecureAccountInfo threeDSecureAccountInfo = new JetpayHostsThreeDSecureAccountInfo();

        threeDSecureAccountInfo
                .setActivityDay(22) // Number of card payment attempts in the last 24 hours.
                .setActivityYear(222) // Number of card payment attempts in the last 365 days.
                .setAdditional("gamer12345") // Additional customer account information, for instance arbitrary customer ID.
                .setAgeIndicator("01") // Number of days since the customer account was created.
                .setDate("01-10-2019") // Account creation date.
                .setChangeDate("01-10-2019") // Last account change date except for password change or password reset.
                .setChangeIndicator("01") // Number of days since last customer account update, not including password change or reset.
                .setPassChangeDate("01") // Last password change or password reset date.
                .setPassChangeIndicator("01-10-2019") // Number of days since the last password change or reset.
                .setProvisionAttempts(16) // Number of attempts to add card details in customer account in the last 24 hours.
                .setPurchaseNumber(12) // Number of purchases with this cardholder account in the previous six months.
                .setAuthData("login_0102") // Any additional log in information in free text.
                .setAuthTime("01-10-2019 13:12") // Account log on date and time.
                .setAuthMethod("01") // Authentication type the customer used to log on to the account when placing the order.
                .setSuspiciousActivity("01") // Suspicious activity detection result.
                .setPaymentAge("01-10-2019") // Card record creation date.
                .setPaymentAgeIndicator("01"); //  Number of days since the payment card details were saved in a customer account.

        JetpayHostsThreeDSecureShippingInfo threeDSecureShippingInfo = new JetpayHostsThreeDSecureShippingInfo();

        threeDSecureShippingInfo
                .setType("01") //Shipment indicator.
                .setDeliveryTime("01") //Shipment terms.
                .setDeliveryEmail("test@gmail.com") // The email to ship purchased digital content, if the customer chooses email delivery.
                .setAddressUsageIndicator("01") // Number of days since the first time usage of the shipping address.
                .setAddressUsage("01-10-2019") // First shipping address usage date in the dd-mm-yyyy format.
                .setCity("Moscow") // Shipping city.
                .setCountry("RU") // Shipping country in the ISO 3166-1 alpha-2 format
                .setAddress("Lenina street 12") // Shipping address.
                .setPostal("109111") // Shipping postbox number.
                .setRegionCode("MOW") // State, province, or region code in the ISO 3166-2 format.
                .setNameIndicator("01"); // Shipment recipient flag.

        JetpayHostsThreeDSecureMpiResultInfo threeDSecureMpiResultInfo = new JetpayHostsThreeDSecureMpiResultInfo();

        threeDSecureMpiResultInfo
                .setAcsOperationId("321412-324-sda23-2341-adf12341234") // The ID the issuer assigned to the previous customer operation and returned in the acs_operation_id parameter inside the callback with payment processing result. Maximum 30 characters.
                .setAuthenticationFlow("01") // The flow the issuer used to authenticate the cardholder in the previous operation and returned in the authentication_flow parameter of the callback with payment processing results.
                .setAuthenticationTimestamp("21323412321324"); // Date and time of the previous successful customer authentication as returned in the mpi_timestamp parameter inside the callback message with payment processing result.

        threeDSecureCustomerInfo
                .setAccountInfo(threeDSecureAccountInfo)
                .setMpiResultInfo(threeDSecureMpiResultInfo)
                .setShippingInfo(threeDSecureShippingInfo);


        threeDSecureInfo.setThreeDSecureCustomerInfo(threeDSecureCustomerInfo);
        threeDSecureInfo.setThreeDSecurePaymentInfo(threeDSecurePaymentInfo);

        paymentInfo.setJetpayHostsThreeDSecureInfo(threeDSecureInfo);
    }

    void configureGooglePayParams(JetpayHostsPaymentInfo paymentInfo) {
        paymentInfo.setMerchantId("your merchant id");
        paymentInfo.setPaymentDataRequest(PaymentDataRequest.fromJson(GooglePayJsonParams.ExampleJSON));
    }
}