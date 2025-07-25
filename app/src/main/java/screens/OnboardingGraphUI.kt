package screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingGraphUI(onboardingModel: OnboardingModel){
    Column(modifier = Modifier.fillMaxWidth()) {

        Spacer(
            modifier = Modifier
                .size(50.dp)
        )
        Image(
            painter = painterResource(id = onboardingModel.image),
            contentDescription = null,
            modifier = Modifier
                .padding(5.dp,0.dp)
                .fillMaxWidth()
                .height(500.dp),
            alignment = Alignment.Center
        )
        Spacer(
            modifier = Modifier
                .size(11.dp)
        )

        Text(
            text = onboardingModel.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground

        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )

        Text(
            text = onboardingModel.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp,0.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )


    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview(){
    OnboardingGraphUI(OnboardingModel.FirstPages)
}
@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview1(){
    OnboardingGraphUI(OnboardingModel.SecondPages)
}
@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview2(){
    OnboardingGraphUI(OnboardingModel.ThirdPages)
}
@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview3(){
    OnboardingGraphUI(OnboardingModel.FourthPages)
}