import { View, Text, StyleSheet } from "react-native";

export default function InfoRow({
    label,
    value,
}: {
    label: string;
    value: string;
}) {
    return (
        <View style={styles.row}>
            {/* Ligne simple label/value utilisée dans plusieurs cartes */}
            <Text style={styles.label}>{label}</Text>
            <Text style={styles.value}>{value}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    row: {
        flexDirection: "row",
        justifyContent: "space-between",
        paddingVertical: 8,
    },
    label: {
        color: "#666",
    },
    value: {
        fontWeight: "600",
    },
});
