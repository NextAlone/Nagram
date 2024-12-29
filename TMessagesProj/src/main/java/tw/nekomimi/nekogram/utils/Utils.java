package tw.nekomimi.nekogram.utils;
import android.util.Pair;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLObject;

public class Utils {
    /**
     * 从请求对象中提取对话 ID 和消息 ID。
     * @param object 请求对象。
     * @return 对话 ID 和消息 ID 的 Pair，或者 null。
     */
    public static Pair<Long, Integer> getDialogIdAndMessageIdFromRequest(TLObject object) {
        if (object instanceof TLRPC.TL_messages_readHistory) {
            TLRPC.TL_messages_readHistory req = (TLRPC.TL_messages_readHistory) object;
            long dialogId = getDialogId(req.peer);
            return new Pair<>(dialogId, req.max_id);
        } else if (object instanceof TLRPC.TL_messages_readMessageContents) {
            TLRPC.TL_messages_readMessageContents req = (TLRPC.TL_messages_readMessageContents) object;
            long dialogId = getDialogId(req.peer);
            return new Pair<>(dialogId, req.id);
        }
        return null;
    }

    /**
     * 从 InputPeer 中提取对话 ID。
     * @param peer 输入对话对象。
     * @return 对话 ID。
     */
    public static long getDialogId(TLRPC.InputPeer peer) {
        if (peer instanceof TLRPC.TL_inputPeerUser) {
            return peer.user_id;
        } else if (peer instanceof TLRPC.TL_inputPeerChat) {
            return -peer.chat_id;
        } else if (peer instanceof TLRPC.TL_inputPeerChannel) {
            return -peer.channel_id;
        }
        return 0;
    }
}

