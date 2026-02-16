import { View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

interface Props {
    icon: keyof typeof Ionicons.glyphMap;
    label: string;
    value: string;
}

export default function AdminStatCard({ icon, label, value }: Props) {
    return (
        <View style={styles.card}>
            {/* Icône + valeur + label : petit widget statistique réutilisable */}
            <Ionicons name={icon} size={28} color="#4db5ff" />
            <Text style={styles.value}>{value}</Text>
            <Text style={styles.label}>{label}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    card: {
        flex: 1,
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 16,
        alignItems: "center",
    },
    value: {
        fontSize: 22,
        fontWeight: "700",
        marginTop: 8,
    },
    label: {
        fontSize: 13,
        color: "#666",
        marginTop: 4,
        textAlign: "center",
    },
});
