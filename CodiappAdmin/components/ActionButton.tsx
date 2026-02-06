import { Ionicons } from "@expo/vector-icons";
import { Pressable, StyleSheet, Text } from "react-native";

export default function ActionButton({
    icon,
    label,
    danger,
    onPress,
}: {
    icon: keyof typeof Ionicons.glyphMap;
    label: string;
    danger?: boolean;
    onPress?: () => void;
}) {
    return (
        <Pressable
            onPress={onPress}
            style={[
                styles.button,
                danger && { backgroundColor: "#ffe5e5" },
            ]}
        >
            <Ionicons
                name={icon}
                size={20}
                color={danger ? "#d11a2a" : "#4db5ff"}
            />
            <Text
                style={[
                    styles.text,
                    danger && { color: "#d11a2a" },
                ]}
            >
                {label}
            </Text>

        </Pressable>
    );
}

const styles = StyleSheet.create({
    button: {
        flexDirection: "row",
        alignItems: "center",
        gap: 12,
        padding: 14,
        borderRadius: 14,
        backgroundColor: "#f2f8ff",
        marginBottom: 10,
    },
    text: {
        fontSize: 15,
        fontWeight: "600",
        color: "#4db5ff",
    },
});
