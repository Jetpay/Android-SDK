package kz.jetpay.msdk.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wallet.PaymentDataRequest
import kz.jetpay.msdk.*
import kz.jetpay.msdk.threeDSecure.*

class MainActivity : AppCompatActivity() {

    private val PAY_ACTIVITY_REQUEST = 888
    private val SECRET = "test_integration_jetpay_salt"
    private val PROJECT_ID = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create payment info with product information
        val paymentInfo = getPaymentInfoOnlyRequiredParams() // getPaymentInfoAllParams

        // enabled google pay
        configureGooglePayParams(paymentInfo)

        // Signature should be generated on your server and delivered to your app
        val signature = SignatureGenerator.generateSignature(paymentInfo.paramsForSignature, SECRET)

        // Sign payment info
        paymentInfo.signature = signature

        // Present Checkout UI
        startActivityForResult(
            JetpayHostsSDK.buildIntent(
                this,
                paymentInfo
            ),
            PAY_ACTIVITY_REQUEST
        )
    }

    // Handle SDK result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAY_ACTIVITY_REQUEST) {
            when (resultCode) {
                JetpayHostsSDK.RESULT_SUCCESS -> {}
                JetpayHostsSDK.RESULT_CANCELLED -> {}
                JetpayHostsSDK.RESULT_DECLINE -> {}
                JetpayHostsSDK.RESULT_FAILED -> {}
            }
            val error = data?.getStringExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_ERROR)
            val token = data?.getStringExtra(JetpayHostsSDK.DATA_INTENT_EXTRA_TOKEN)
            val sessionError = data?.getStringExtra(JetpayHostsSDK.DATA_INTENT_SESSION_INTERRUPTED)
        }
    }

    // Payment Info
    private fun getPaymentInfoOnlyRequiredParams(): JetpayHostsPaymentInfo {
        return JetpayHostsPaymentInfo(
            PROJECT_ID, // project ID that is assigned to you
            "test_integration_jetpay_id", // payment ID to identify payment in your system
            100, // 1.00
            "USD"
        )
    }

    private fun getPaymentInfoAllParams(): JetpayHostsPaymentInfo {
        return JetpayHostsPaymentInfo(
            PROJECT_ID, // project ID that is assigned to you
            "test_integration_jetpay_id", // payment ID to identify payment in your system
            100, // 1.00
            "USD",
            "T-shirt with dog print",
            "10", // unique ID assigned to your customer
            ""
        )
    }

    // Additional
    internal fun setDMSPayment(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.action = JetpayHostsPaymentInfo.ActionType.Auth
    }

    internal fun setActionTokenize(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.action = JetpayHostsPaymentInfo.ActionType.Tokenize
    }

    internal fun setActionVerify(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.action = JetpayHostsPaymentInfo.ActionType.Verify
    }

    internal fun getAction(paymentInfo: JetpayHostsPaymentInfo): JetpayHostsPaymentInfo.ActionType {
        return paymentInfo.action
    }

    internal fun setToken(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.token = "token"
    }

    internal fun setReceiptData(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.receiptData = "receipt data"
    }

    // if you want to hide the saved cards, pass the value - true
    internal fun setHideSavedWallets(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.hideSavedWallets = false
    }

    // For forced opening of the payment method, pass its code. Example: qiwi, card ...
    internal fun setForcePaymentMethod(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.forcePaymentMethod = "card"
    }

    internal fun setRecurrent(paymentInfo: JetpayHostsPaymentInfo) {
        val recurrentInfo = JetpayHostsRecurrentInfo(
            "R", // type
            "20", // expiry day
            "11", // expiry month
            "2030", // expiry year
            "M", // recurrent period
            "12:00:00", // start time
            "12-02-2020", // start date
            "your_recurrent_id") // recurrent payment ID

        // Additional options if needed
        recurrentInfo.setAmount(1000)
        recurrentInfo.setSchedule(
            arrayOf(
                JetpayHostsRecurrentInfoSchedule("20-10-2020", 1000),
                JetpayHostsRecurrentInfoSchedule("20-10-2020", 1000)
            )
        )

        paymentInfo.setRecurrent(recurrentInfo)
    }
    internal fun getRecurrent(paymentInfo: JetpayHostsPaymentInfo): JetpayHostsRecurrentInfo {
        return paymentInfo.jetpayHostsRecurrentInfo
    }

    internal fun setKnownAdditionalFields(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.jetpayHostsAdditionalFields = arrayOf(
            JetpayHostsAdditionalField(JetpayHostsAdditionalFieldEnums.AdditionalFieldType.customer_first_name, "Mark"),
            JetpayHostsAdditionalField(JetpayHostsAdditionalFieldEnums.AdditionalFieldType.billing_country, "US"))
    }

    internal fun getKnownAdditionalFields(paymentInfo: JetpayHostsPaymentInfo): Array<JetpayHostsAdditionalField> {
        return paymentInfo.jetpayHostsAdditionalFields
    }

    internal fun setScreenDisplayModes(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.setJetpayHostsScreenDisplayMode(
            arrayListOf(
                JetpayHostsScreenDisplayMode.HIDE_DECLINE_FINAL_PAGE,
                JetpayHostsScreenDisplayMode.HIDE_SUCCESS_FINAL_PAGE
            ))
    }

    internal fun addScreenDisplayModes(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo
            .addJetpayHostsScreenDisplayMode("hide_decline_final_page")
            .addJetpayHostsScreenDisplayMode("hide_success_final_page")
    }

    // Setup 3D Secure parameters
    internal fun setupThreeDSecureParams(paymentInfo: JetpayHostsPaymentInfo) {
        val threeDSecureInfo = JetpayHostsThreeDSecureInfo()

        val threeDSecurePaymentInfo = JetpayHostsThreeDSecurePaymentInfo()

        threeDSecurePaymentInfo
            .setReorder("01") // This parameter indicates whether the cardholder is reordering previously purchased merchandise.
            .setPreorderPurchase("01") // This parameter indicates whether cardholder is placing an order for merchandise with a future availability or release date.
            .setPreorderDate("01-10-2019") // The date the preordered merchandise will be available.Format: dd-mm-yyyy.
            .setChallengeIndicator("01") // This parameter indicates whether challenge flow is requested for this payment.
            .challengeWindow = "01" // The dimensions of a window in which authentication page opens.

        val threeDSecureGiftCardInfo = JetpayHostsThreeDSecureGiftCardInfo()

        threeDSecureGiftCardInfo
            .setAmount(12345) // Amount of payment with prepaid or gift card denominated in minor currency units.
            .setCurrency("USD") // Currency of payment with prepaid or gift card in the ISO 4217 alpha-3 format
            .count = 1 // Total number of individual prepaid or gift cards/codes used in purchase.

        threeDSecurePaymentInfo.giftCard = threeDSecureGiftCardInfo // object with information about payment with prepaid card or gift card.

        val threeDSecureCustomerInfo = JetpayHostsThreeDSecureCustomerInfo()
        threeDSecureCustomerInfo
            .setAddressMatch("Y") //The parameter indicates whether the customer billing address matches the address specified in the shipping object.
            .setHomePhone("79105211111") // Customer home phone number.
            .setWorkPhone("73141211111") // Customer work phone number.
            .billingRegionCode = "ABC" // State, province, or region code in the ISO 3166-2 format. Example: SPE for Saint Petersburg, Russia.

        val threeDSecureAccountInfo = JetpayHostsThreeDSecureAccountInfo() // object with account information on record with merchant

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
            .paymentAgeIndicator = "01" //  Number of days since the payment card details were saved in a customer account.

        val threeDSecureShippingInfo = JetpayHostsThreeDSecureShippingInfo() // object that contains shipment details

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
            .nameIndicator = "01" // Shipment recipient flag.

        val threeDSecureMpiResultInfo = JetpayHostsThreeDSecureMpiResultInfo() // object that contains information about previous customer authentication

        threeDSecureMpiResultInfo
            .setAcsOperationId("321412-324-sda23-2341-adf12341234") // The ID the issuer assigned to the previous customer operation and returned in the acs_operation_id parameter inside the callback with payment processing result. Maximum 30 characters.
            .setAuthenticationFlow("01") // The flow the issuer used to authenticate the cardholder in the previous operation and returned in the authentication_flow parameter of the callback with payment processing results.
            .authenticationTimestamp = "21323412321324" // Date and time of the previous successful customer authentication as returned in the mpi_timestamp parameter inside the callback message with payment processing result.

        threeDSecureCustomerInfo
            .setAccountInfo(threeDSecureAccountInfo) // object with account information on record with merchant
            .setMpiResultInfo(threeDSecureMpiResultInfo) // object that contains information about previous customer authentication
            .shippingInfo = threeDSecureShippingInfo // object that contains shipment details


        threeDSecureInfo.threeDSecureCustomerInfo = threeDSecureCustomerInfo
        threeDSecureInfo.threeDSecurePaymentInfo = threeDSecurePaymentInfo

        paymentInfo.jetpayHostsThreeDSecureInfo = threeDSecureInfo
    }

    internal fun getThreeDSecureInfo(paymentInfo: JetpayHostsPaymentInfo): JetpayHostsThreeDSecureInfo {
        return paymentInfo.jetpayHostsThreeDSecureInfo
    }

    fun configureGooglePayParams(paymentInfo: JetpayHostsPaymentInfo) {
        paymentInfo.merchantId = "your merchant id"
        paymentInfo.paymentDataRequest = PaymentDataRequest.fromJson(GooglePayJsonParams.getJSON())
    }
}