package screens

//noinspection SuspiciousImport
import androidx.annotation.DrawableRes
import com.happybirthday.taacheck.R

sealed class OnboardingModel(
        @DrawableRes val image: Int,
    val title: String,
    val description: String,
        ){
    data object FirstPages : OnboardingModel(
        image = R.drawable.taacheck_welcome,
        title = "Welcome to TaaCheck",
        description = "Your Go To Electrical App",
        )
    data object SecondPages : OnboardingModel(
        image = R.drawable.taacheck_caution,
        title = "Reporting",
        description = "Create posts on elctrical related issues and post them",
    )data object ThirdPages : OnboardingModel(
        image = R.drawable.taacheck_service,
        title = "Service Providers",
        description = "Find service providers that specialise in electrical based products and activities",
    )data object FourthPages : OnboardingModel(
        image = R.drawable.taacheck_start,
        title = "Get Started",
        description = "Dive Right in",
    )

}