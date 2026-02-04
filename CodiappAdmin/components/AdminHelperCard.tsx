import { View, Text, StyleSheet, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Helper } from "@/types/Helper";
import { useRouter } from "expo-router";

interface AdminHelperCardProps {
    helper: Helper;
}

export default function AdminHelperCard({ helper }: AdminHelperCardProps) {
    const router = useRouter();
    const isAvailable = helper.status === "available";

    return (
        <Pressable
            style={styles.card}
            onPress={() => router.push(`/(admin)/helpers/${helper.id}`)}
        >
            {/* Top row */}
            <View style={styles.topRow}>
                <Text style={styles.name}>{helper.name}</Text>

                <View
                    style={[
                        styles.statusBadge,
                        {
                            backgroundColor: isAvailable ? "#d1fae5" : "#e5e7eb",
                        },
                    ]}
                >
                    <Text
                        style={{
                            color: isAvailable ? "#065f46" : "#374151",
                            fontWeight: "600",
                            fontSize: 12,
                        }}
                    >
                        {isAvailable ? "Disponible" : "Indisponible"}
                    </Text>
                </View>
            </View>

            {/* Infos */}
            <View style={styles.infoRow}>
                <View style={styles.infoItem}>
                    <Ionicons name="location" size={16} color="#555" />
                    <Text style={styles.infoText}>
                        {helper.maxDistance} km
                    </Text>
                </View>

                <View style={styles.infoItem}>
                    <Ionicons name="stats-chart" size={16} color="#555" />
                    <Text style={styles.infoText}>
                        {helper.helpsThisMonth} aides ce mois
                    </Text>
                </View>
            </View>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    card: {
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 16,
    },
    topRow: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 12,
    },
    name: {
        fontSize: 16,
        fontWeight: "700",
    },
    statusBadge: {
        paddingHorizontal: 10,
        paddingVertical: 4,
        borderRadius: 12,
    },
    infoRow: {
        flexDirection: "row",
        justifyContent: "space-between",
    },
    infoItem: {
        flexDirection: "row",
        alignItems: "center",
        gap: 6,
    },
    infoText: {
        fontSize: 14,
        color: "#555",
        fontWeight: "500",
    },
});
