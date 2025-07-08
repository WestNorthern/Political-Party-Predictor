@echo off
REM === Clean previous build ===
if exist bin (
    echo Removing old build...
    rmdir /s /q bin
)
mkdir bin

REM === Compile all .java files under src ===
echo Compiling Java sources...
for /R src %%F in (*.java) do (
    javac -cp "lib\*" -d bin "%%F"
    if errorlevel 1 (
        echo Compilation error in %%F
        exit /b 1
    )
)

echo.
echo Build complete. Classes are in bin\
