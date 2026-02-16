import { View, Text, StyleSheet } from "react-native";
import InfoRow from "./InfoRow";

export default function UserInfoCard({ userId }: { userId?: string }) {
    return (
        <View style={styles.card}>
            {/* Carte d'informations basique pour l'utilisateur (exemples statiques) */}
            <Text style={styles.name}>Utilisateur #{userId}</Text>
            <Text style={styles.role}>Aidé</Text>

            <InfoRow label="Email" value="user@email.com" />
            <InfoRow label="Téléphone" value="06 00 00 00 00" />
            <InfoRow label="Rayon d’aide" value="2 km" />
        </View>
    );
}

const styles = StyleSheet.create({
    card: {
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 16,
        marginHorizontal: 16,
        marginBottom: 24,
    },
    name: {
        fontSize: 18,
        fontWeight: "700",
        marginBottom: 4,
    },
    role: {
        color: "#666",
        marginBottom: 12,
    },
});
