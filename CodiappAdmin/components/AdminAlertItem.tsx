import { View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export default function AdminAlertItem({ text }: { text: string }) {
    return (
        <View style={styles.row}>
            <Ionicons name="alert-circle" size={20} color="#ff5252" />
            <Text style={styles.text}>{text}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    row: {
        flexDirection: "row",
        alignItems: "center",
        gap: 12,
        paddingVertical: 10,
    },
    text: {
        fontSize: 14,
        color: "#333",
    },
});
