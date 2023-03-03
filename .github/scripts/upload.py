import requests
import sys
import os

def read_env(key):
    if key in os.environ:
        return os.environ[key]
    else:
        print(f"failed to read env {key}")
        return ""

APK_FOLDER = "apks"
API_PREFIX = "http://127.0.0.1:38118/bot" + os.environ["BOT_TOKEN"] + "/"

BOT_TARGET = read_env("BOT_TARGET")
ADMIN_USERID = BOT_TARGET.replace("-100","")

VERSION_NAME = read_env("VERSION_NAME")
VERSION_CODE = read_env("VERSION_CODE")
COMMIT_HASH = read_env("GITHUB_SHA")
COMMIT_MESSAGE = read_env("COMMIT_MESSAGE")

APK_CHANNEL_ID = "@miaomiao_apks"
UPDATE_CHANNEL_ID = "@miaomiao_apks"
UPDATE_METADATA_CHANNEL_ID = "@miaomiao_metadata"
CI_CHANNEL_ID = "@NekoX_CI"

def generateReleaseMessage(first_apk_message_id, release_text) -> str:
    rel_type = 'PRE_RELEASE' if 'preview' in VERSION_NAME else 'RELEASE'
    return f"""
#{rel_type} [ ](https://t.me/{APK_CHANNEL_ID.replace("@","")}/{first_apk_message_id + 1}) *{VERSION_NAME}*

{release_text}

[GitHub Release](https://github.com/NekoX-Dev/NekoX/releases/{VERSION_NAME}) | [Apks](https://t.me/{APK_CHANNEL_ID.replace("@","")}/{first_apk_message_id}) | [Check Update](tg://update/)
"""

def waitReply(mid):
    last_update = 0
    while True:
        print(f"Waiting reply for {mid} offset {last_update}...")
        resp = requests.post(API_PREFIX + "getUpdates", json={"allowed_updates":["message"], "timeout": 20, "offset": last_update + 1})
        resp = resp.json()
        if not resp["ok"]:
            continue
        resp = resp["result"]
        for update in resp:
            if 'message' in update:
                msg = update["message"]
                if 'from' in msg and str(msg['from']["id"]) == ADMIN_USERID:
                    if 'reply_to_message' in msg and str(msg["reply_to_message"]["message_id"]) == str(mid):
                        return msg
        for update in resp:
            last_update = max(last_update, update["update_id"])


def sendMessage(message, user_id = BOT_TARGET) -> int:
    data = {
        "chat_id" : user_id,
        "text": message,
        "parse_mode": "Markdown"
    }
    resp = requests.post(API_PREFIX + "sendMessage", json=data).json()
    print(resp)
    return int(resp["result"]["message_id"])


def sendDocument(user_id, path, message = ""):
    files = {'document': open(path, 'rb')}
    data = {'chat_id': user_id, 'caption': message, 'parse_mode': 'Markdown'}
    response = requests.post(API_PREFIX + "sendDocument", files=files, data=data)
    print(response.json())


def sendRelease():
    apks = os.listdir(APK_FOLDER)
    apks.sort()
    print(apks)

    # read message from admin
    mid = sendMessage(f"Please reply the release message for the version {VERSION_NAME},{VERSION_CODE}:", user_id=BOT_TARGET)
    admin_resp = waitReply(mid)
    print(admin_resp)
    release_text = admin_resp["text"]

    # send message and apks to APK channel
    message = f"=== {VERSION_NAME} ==="
    apk_channel_first_id = sendMessage(message, user_id=APK_CHANNEL_ID)

    for apk in apks:
        path = os.path.join(APK_FOLDER, apk)
        sendDocument(user_id=APK_CHANNEL_ID, path=path)
    
    # generate release message and send to update channel
    release_msg = generateReleaseMessage(apk_channel_first_id, release_text)
    sendMessage(release_msg, user_id=UPDATE_CHANNEL_ID)

    # send release message to metadata channel
    mid = sendMessage(release_text, user_id=UPDATE_METADATA_CHANNEL_ID)
    meta_msg = f"{VERSION_NAME},{VERSION_CODE},{apk_channel_first_id},{mid}"
    sendMessage(meta_msg, user_id=UPDATE_METADATA_CHANNEL_ID)


def sendCIRelease():
    apks = os.listdir(APK_FOLDER)
    apks.sort()
    apk = os.path.join(APK_FOLDER, apks[0])
    message = f"CI Build\n\n{COMMIT_MESSAGE}\n\n{COMMIT_HASH[0:8]}"
    sendDocument(user_id=CI_CHANNEL_ID, path = apk, message=message)


if __name__ == '__main__':
    print(sys.argv)
    if len(sys.argv) != 2:
        print("Run Type: release, ci, debug")
        exit(1)
    mode = sys.argv[1]
    try:
        if mode == "release":
            sendRelease()
        elif mode == "ci":
            if COMMIT_MESSAGE.startswith("ci"):
                CI_CHANNEL_ID = BOT_TARGET
            sendCIRelease()
        elif mode == "debug":
            APK_CHANNEL_ID = "@test_channel_nekox"
            UPDATE_CHANNEL_ID = "@test_channel_nekox"
            UPDATE_METADATA_CHANNEL_ID = "@test_channel_nekox"
            sendRelease()
        else:
            print("unknown mode")
            exit(1)
    except Exception as e:
        print(e)
        exit(1)

