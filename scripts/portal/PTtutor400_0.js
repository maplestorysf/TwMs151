load('nashorn:mozilla_compat.js');
importPackage(Packages.tools.packet);

function enter(pi) {
    pi.getClient().getSession().write(CField.NPCPacket.getNPCTalk(1402001, 0, "嗯..已經開始了嗎?分秒都不能有差錯。", "00 00", 17, 1402001));
    return true;
}