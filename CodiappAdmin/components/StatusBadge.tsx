import { View, Text, StyleSheet } from "react-native";
import { UserStatus } from "@/types/User";

export default function UserStatusBadge({ status }: { status: UserStatus }) {
    const config = {
        active: { label: "Actif", color: "#22c55e" },
        suspended: { label: "Suspendu", color: "#ef4444" },
        waiting: { label: "En attente", color: "#f59e0b" },
    };

    return (
        <View style={[styles.badge, { backgroundColor: config[status].color }]}>
            <Text style={styles.text}>{config[status].label}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    badge: {
        paddingHorizontal: 10,
        paddingVertical: 4,
        borderRadius: 12,
    },
    text: {
        color: "#fff",
        fontSize: 12,
        fontWeight: "600",
    },
});
