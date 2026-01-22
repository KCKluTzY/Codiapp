import { View, Text, StyleSheet } from "react-native";

interface StatsCardProps {
    value: number;
    label: string;
}

export default function StatsCard({ value, label }: StatsCardProps) {
    return (
        <View style={styles.card}>
            <Text style={styles.value}>{value}</Text>
            <Text style={styles.label}>{label}</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    card: {
        flex: 1,
        backgroundColor: "#8B3DFF",
        borderRadius: 16,
        paddingVertical: 20,
        marginHorizontal: 6,
        alignItems: "center",
        justifyContent: "center",
    },
    value: {
        color: "white",
        fontSize: 26,
        fontWeight: "800",
        marginBottom: 4,
    },

    label: {
        color: "white",
        fontSize: 14,
        textAlign: "center",
        fontWeight: "600",
    }
});