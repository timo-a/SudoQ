package de.sudoq.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.sudoq.R
import de.sudoq.model.game.GameManager
import de.sudoq.model.persistence.xml.game.IGamesListRepo
import de.sudoq.model.profile.ProfileManager
import de.sudoq.persistence.game.GameRepo
import de.sudoq.persistence.game.GamesListRepo
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo
import de.sudoq.persistence.sudoku.SudokuRepoProvider
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideProfileManager(@ApplicationContext context: Context): ProfileManager {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver")
        val profilesDir = context.getDir(context.getString(R.string.path_rel_profiles), Context.MODE_PRIVATE)
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        
        if (pm.noProfiles()) {
            pm.initialize(ProfileManager.DEFAULT_PROFILE_NAME)
        } else {
            pm.loadCurrentProfile()
        }
        
        return pm
    }

    @Provides
    @Singleton
    fun provideSudokuTypeRepo(@ApplicationContext context: Context): SudokuTypeRepo {
        val sudokuDir = context.getDir(context.getString(R.string.path_rel_sudokus), Context.MODE_PRIVATE)
        return SudokuTypeRepo(sudokuDir)
    }

    @Provides
    fun provideGameRepo(profileManager: ProfileManager, sudokuTypeRepo: SudokuTypeRepo): GameRepo {
        return GameRepo(
            profileManager.profilesDir,
            profileManager.currentProfileID,
            sudokuTypeRepo
        )
    }

    @Provides
    fun provideGamesListRepo(profileManager: ProfileManager): IGamesListRepo {
        val gamesFile = File(profileManager.currentProfileDir, "games.xml")
        val gamesDir = File(profileManager.currentProfileDir, "games")
        return GamesListRepo(gamesDir, gamesFile)
    }

    @Provides
    fun provideGameManager(
        profileManager: ProfileManager,
        gameRepo: GameRepo,
        gamesListRepo: IGamesListRepo,
        sudokuTypeRepo: SudokuTypeRepo
    ): GameManager {
        return GameManager(profileManager, gameRepo, gamesListRepo, sudokuTypeRepo)
    }

    @Provides
    @Singleton
    fun provideSudokuRepoProvider(@ApplicationContext context: Context, sudokuTypeRepo: SudokuTypeRepo): SudokuRepoProvider {
        val sudokuDir = context.getDir(context.getString(R.string.path_rel_sudokus), Context.MODE_PRIVATE)
        return SudokuRepoProvider(sudokuDir, sudokuTypeRepo)
    }
}
