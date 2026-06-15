import axios from "@/axios";

// 新建对话
export function newChat(message) {
    return axios.post("/chat/new", { message })
}

