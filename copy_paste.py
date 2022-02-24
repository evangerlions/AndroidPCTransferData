import os
import pyperclip
import subprocess

TEMP_FILE_NAME = "temp_trans_file"
PHONE_FILE_PATH = "/storage/emulated/0/"
START_APPLICATION = 'adb shell am start -n "com.zhoukai.copytextfrompc/com.zhoukai.copytextfrompc.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER'


def startCopyAndPaste():
    s = pyperclip.paste()
    print(s)
    filename = createTransFile(s)
    sendFileToPhone(filename)


def createTransFile(s):
    filename = ""
    with open(TEMP_FILE_NAME, "w") as f:
        f.write(s)
        filename = f.name
    return filename


def sendFileToPhone(filename):
    if not os.path.isfile(filename):
        raise RuntimeError(f"{filename} is not exist")

    command = f"adb push {filename} {PHONE_FILE_PATH}"
    runCommand(command)
    runCommand(START_APPLICATION)
    


def runCommand(command):
    print(f"exec: \n{command}")
    subprocess.check_call(command, shell=True)


startCopyAndPaste()
