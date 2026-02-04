import { View, Text, StyleSheet } from "react-native";
import { User } from "@/types/User";
import UserStatusBadge from "./StatusBadge";
import UserActionRow from "./UserActionRow";
import { useRouter } from "expo-router";

export default function AdminUserCard({ user }: { user: User }) {
    const router = useRouter();

    return (
        <View style={styles.card}>
            <View style={styles.header}>
                <Text style={styles.name}>{user.name}</Text>
                <UserStatusBadge status={user.status} />
            </View>

            <Text style={styles.meta}>
                {user.age} ans • {user.city}
            </Text>

            <Text style={styles.tutor}>
                Tuteur : {user.tutor ?? "Aucun"}
            </Text>

            <UserActionRow onView={() => router.push(`/(admin)/users/${user.id}`)} />
        </View>
    );
}

const styles = StyleSheet.create({
    card: {
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 16,
        marginHorizontal: 16,
        marginVertical: 8,
    },
    header: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
    },
    name: {
        fontSize: 16,
        fontWeight: "700",
    },
    meta: {
        color: "#666",
        marginTop: 4,
    },
    tutor: {
        marginTop: 6,
        fontWeight: "500",
    },
});
