@echo off
echo ===================================
echo  Streets of Rage - Java OOP Port
echo  Build Script
echo ===================================
echo.

echo [1/2] Compiling all Java sources...
if not exist out mkdir out
javac -d out -sourcepath src ^
    src/streetsofrage/main/Game.java ^
    src/streetsofrage/main/GamePanel.java ^
    src/streetsofrage/entity/Entity.java ^
    src/streetsofrage/entity/Player.java ^
    src/streetsofrage/entity/Enemy.java ^
    src/streetsofrage/graphics/SpriteSheet.java ^
    src/streetsofrage/graphics/Animation.java ^
    src/streetsofrage/graphics/SpriteLoader.java ^
    src/streetsofrage/inputs/KeyHandler.java ^
    src/streetsofrage/audio/AudioManager.java ^
    src/streetsofrage/combat/Attack.java ^
    src/streetsofrage/combat/AttackController.java ^
    src/streetsofrage/combat/HitBox.java ^
    src/streetsofrage/level/Level.java ^
    src/streetsofrage/ui/HUD.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/2] Compilation successful!
echo.
echo ===================================
echo  Run the game with: run.bat
echo ===================================
pause
