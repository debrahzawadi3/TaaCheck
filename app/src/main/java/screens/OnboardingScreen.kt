package com.happybirthday.taacheck.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import screens.OnboardingGraphUI
import kotlinx.coroutines.launch
import screens.OnboardingModel

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val scope = rememberCoroutineScope()

    val pages: List<OnboardingModel> = listOf(
        OnboardingModel.FirstPages, OnboardingModel.SecondPages, OnboardingModel.ThirdPages,
        OnboardingModel.FourthPages
    )
    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage){
                0 -> listOf("","NEXT")
                1 -> listOf("BACK","NEXT")
                2 -> listOf("BACK","NEXT")
                3 -> listOf("BACK","START")
                else -> listOf("","")
            }
        }
    }
    Scaffold (
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp,10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                    ) {
                    if (buttonState.value[0].isNotEmpty()) {
                        ButtonUI(text = buttonState.value[0],
                            backgroundColor = Color.Transparent,
                            textColor = Color.Gray) {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center) {
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                }
                Box(modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd) {
                    ButtonUI(text = buttonState.value[1],
                        backgroundColor = Color(0xFF2196F3),
                        textColor = Color.White) {
                        scope.launch {
                            if (pagerState.currentPage < pages.size - 1){
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinished()
                            }
                        }

                    }
                }
            }
        }
        ,
        content = {
            Column(Modifier.padding(it)) {
                HorizontalPager(state = pagerState) { index ->
                    OnboardingGraphUI(onboardingModel = pages[index])
                }
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview(){
    OnboardingScreen{

    }
}