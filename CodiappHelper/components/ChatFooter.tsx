import { MaterialIcons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Pressable, StyleSheet, TextInput, View } from "react-native";

export default function ChatFooter() {
    const [message, setMessage] = useState("");

    const canSend = message.trim().length > 0;
    const router = useRouter();
    return (
        <View style={styles.container}>
            <Pressable style={styles.iconBtn}>
                <MaterialIcons
                    name="photo-camera"
                    size={28}
                    color="#9f44ef"
                />
            </Pressable>
            <Pressable style={styles.iconBtn}>
                <MaterialIcons
                    name="keyboard-voice"
                    size={28}
                    color="#2ecc71"
                />
            </Pressable>

            {/* Input */}
            <TextInput
                style={styles.input}
                placeholder="Écrire un message..."
                placeholderTextColor="#888"
                value={message}
                onChangeText={setMessage}
                multiline
            />
            <Pressable
                style={[
                    styles.sendBtn,
                    !canSend && styles.sendBtnDisabled,
                ]}
                disabled={!canSend}
            >
                <MaterialIcons
                    name="send"
                    size={26}
                    color="#fff"
                />
            </Pressable>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        alignItems: "center",
        paddingHorizontal: 16,
        paddingVertical: 10,
        borderTopWidth: 1,
        borderTopColor: "#e3e3e3",
        backgroundColor: "#fff",
    },

    iconBtn: {
        padding: 8,
        marginRight: 6,
    },

    input: {
        flex: 1,
        minHeight: 40,
        maxHeight: 100,
        paddingHorizontal: 12,
        paddingVertical: 8,
        fontSize: 15,
        borderRadius: 8,
        backgroundColor: "#f4f4f4",
        marginHorizontal: 6,
    },

    sendBtn: {
        padding: 10,
        borderRadius: 8,
        backgroundColor: "#44b0ef",
        justifyContent: "center",
        alignItems: "center",
    },

    sendBtnDisabled: {
        backgroundColor: "#a6ddef",
    },
});
