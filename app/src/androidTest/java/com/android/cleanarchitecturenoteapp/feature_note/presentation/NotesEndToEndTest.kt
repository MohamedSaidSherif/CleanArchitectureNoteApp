package com.android.cleanarchitecturenoteapp.feature_note.presentation

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.core.app.ApplicationProvider
import com.android.cleanarchitecturenoteapp.R
import com.android.cleanarchitecturenoteapp.core.util.TestTags
import com.android.cleanarchitecturenoteapp.di.AppModule
import com.android.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.android.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesScreen
import com.android.cleanarchitecturenoteapp.feature_note.presentation.util.Screen
import com.android.cleanarchitecturenoteapp.ui.theme.CleanArchitectureNoteAppTheme
import com.android.cleanarchitecturenoteapp.ui.theme.Violet
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    private lateinit var context: Context

    @get: Rule(order = 0)
    val hiltAndRule = HiltAndroidRule(this)

    @get: Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        hiltAndRule.inject()
        composeRule.setContent {
            CleanArchitectureNoteAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ) {
                    composable(Screen.NotesScreen.route) {
                        NotesScreen(navController = navController)
                    }
                    composable(
                        route = Screen.AddEditNoteScreen.route +
                                "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterWards() {
        //Click on FAB to add new note
        composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_note_floating_action_button))
            .performClick()
        //Enter texts in title and content text fields
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput("test-content")
        // Click on Violet color
        composeRule.onNodeWithTag("$Violet").performClick()
        // Save the note
        composeRule.onNodeWithContentDescription(context.getString(R.string.floating_action_button_save_note))
            .performClick()

        // Make sure there is a note in the list with our title and content
        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        // Click on a note to edit it
        composeRule.onNodeWithText("test-title").performClick()

        // Make sure title and content text fields contain note title and content
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextEquals("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).assertTextEquals("test-content")
        // Change title text
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("2")
        // Click on FAB to update the note
        composeRule.onNodeWithContentDescription(context.getString(R.string.floating_action_button_save_note))
            .performClick()

        // Make sure the update was applied to the list
        composeRule.onNodeWithText("test-title2").assertIsDisplayed()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        for (i in 1..3) {
            //Click on FAB to add new note
            composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_note_floating_action_button))
                .performClick()
            //Enter texts in title and content text fields
            composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(i.toString())
            composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput(i.toString())
            // Click on Violet color
            composeRule.onNodeWithTag("$Violet").performClick()
            // Save the note
            composeRule.onNodeWithContentDescription(context.getString(R.string.floating_action_button_save_note))
                .performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule.onNodeWithContentDescription(context.getString(R.string.toggle_sort_section_icon))
            .performClick()
        composeRule.onNodeWithContentDescription("Title").performClick()
        composeRule.onNodeWithContentDescription("Descending").performClick()

        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[0].assertTextContains("3")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[1].assertTextContains("2")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[2].assertTextContains("1")
    }
}