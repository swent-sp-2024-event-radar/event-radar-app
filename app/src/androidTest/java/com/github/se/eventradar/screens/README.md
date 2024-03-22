# Signature Checks

In order for your app to be compatible with our automated testing, we provide you with a signature file `SignatureChecks.kt` that will ensure that you have the same API that we use. This file should be placed at the base of the kotlin code of your app (`main/java/com.github.se.bootcamp`).

> [!CAUTION]
> Please make sure that no error remains in this file at the end of the bootcamp.

# Test Screens

For testing and **grading**, we will use Kaspresso. We recommend that you use it for your own testing. To make our grading compatible with your implementation, please place the following files in the `com/github/se/bootcamp/screens (androidTest)` folder:
- `CreateTaskScreen.kt`
- `EditTaskScreen.kt`
- `MainActivityScreen.kt`
- `LoginScreen.kt`

You will have to add test tags or any preferred way you want to select your components, according to the [Figma](https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1). These files will be part of your handout, so feel free to change the selectors.



> [!NOTE]
> See the tutorial on [Testing](../bootcamp/tutorials/Testing.md) to understand how Kaspresso works.
