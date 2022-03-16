# Yandex Pay Classic for Android

Yandex Pay Mobile Library for Android

## Requirements

- Android 6.0 (API 23)
- Yandex OAuth ID which can be obtained [here](https://oauth.yandex.ru/)

## Installation
The library is located in Maven Central repository.
- Add `mavenCentral()` as a dependency repository to your android project:
```gradle
allprojects {
  repositories {
    // ...
    mavenCentral()
  }
}
```
- Include `com.yandex.pay:core:0.1.1` as `implementation` in `dependencies` section of `build.gradle` script:
```gradle
dependencies {
  // ...
  implementation 'com.yandex.pay:core:0.1.1`
}
```
- Set `YANDEX_CLIENT_ID` which was obtained from `oauth.yandex.ru` in your `build.gradle` as a manifest placeholder:
```gradle
android {
  // ...
  defaultConfig {
    // ...
    manifestPlaceholders += [
      // ...
      YANDEX_CLIENT_ID: "12345678901234567890", // Put your YANDEX_CLIENT_ID here
    ]
  }
}
```

## Usage

## Initialization
The initialization step must be done before any use of `YandexPayLib` including the Yandex Pay button display.
```kotlin
if (YandexPayLib.isSupported) {
  YandexPayLib.initialize(
    YandexPayLibConfig(
      logging = true, // Should the library log events in logcat
    ),
    this // Application Context,
  )
}
```

### Display Yandex Pay button
Below is the sample code to display the button:
```xml
<LinearLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  ...
>
<!-- ... -->
  <com.yandex.pay.core.ui.YandexPayButton
    android:id="@+id/button"
    app:yandexpay_personalization="last_value"
    android:layout_marginTop="50dp"
    android:layout_width="330dp"
    android:layout_height="54dp" />
</LinearLayout>
```

`app:yandexpay_personalization` allows to customize the button display/personalization mode:
- `none`: the button is not personalized. It displays the prompt "Pay with Y.Pay"
- `last_value`: the button is personalized. It displays the Yandex Pay logo, the last card that was used with Yandex Pay and a user's userpic. When used for the first time, the button looks like it's in the mode `none`
- `updating`: the mode is similar to `last_value`, but when it's added on a window, `onAttachedToWindow` method will try to validate the previously used card for payment and fetch a new userpic, if any.

### Handle the button interaction
To handle the button interactions a click handler must be specified:
```kotlin
val button = findViewById(R.id.button)
button.setOnClickListener { intentBuilder: IntentBuilder ->
  val intent = intentBuilder.setOrderDetails(
    Merchant(
      MerchantID.from("15a919d7-c990-412c-b5eb-8d1ffe60e68a"), // Merchant ID
      "MERCHANT_NAME", // Merchant name to display to a user
      "https://merchant.com/", // Merchant Origin
    ),
    Order( // Order details
      OrderID.from("ORDER1"), // Order ID
      Price.from("150000.00"), // Total price for all items combined
      "ORDER 1", // Order label to display to a user
      listOf( // Order items
        OrderItem(
          Price.from("50000.00"), // Price of the item
          "ITEM1", // Item label to display to a user
        ),
        OrderItem(
          Price.from("100000.00"),
          "ITEM2",
        ),
      ),
    ),
    listOf( // a list of payment methods available with your PSP
      PaymentMethod(
        listOf(AuthMethod.PanOnly), // What the payment token will contain: encrypted card details or a card token
        PaymentMethodType.Card, // Currently it's a single supported payment method: CARD
        Gateway.from("gatewayID"), // PSP Gateway ID
        listOf(CardNetwork.Visa, CardNetwork.MasterCard), // Payment networks supported by the PSP
        GatewayMerchantID.from("MerchantGW1"), // Merchant ID with the PSP
      ),
    ),
  ).build()
  // After the intent is built, an activity must be displayed with that intent.
  startActivityForResult(intent, TOKEN_REQUEST_CODE)
  // After the activity is done working the result must be interpreted with `YandexPayLib.instance.processActivityResult`
}
```

### Process the activity result
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
  if (requestCode == TOKEN_REQUEST_CODE) {
    val token = YandexPayLib.instance.processActivityResult(context, resultCode, data)
    if (token != null) {
      token
        .onSuccess { paymentCheckoutResult ->
          val token = paymentCheckoutResult.paymentToken
           // Pass the token to your PSP
        }
        .onFailure { error ->
          val message = error.message
          // Process the error
        }
    }
}
```

### Process the payment token with your PSP
The payment token that was obtained in the previous snippet should be then passed to your PSP for processing.
