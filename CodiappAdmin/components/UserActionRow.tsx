import { View, Pressable, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export default function UserActionRow({ onView }: { onView: () => void }) {
    return (
        <View style={styles.row}>
            {/* Actions rapides : 'Voir' actif, autres visuellement désactivées */}
            <Pressable onPress={onView} style={styles.action}>
                <Ionicons name="eye" size={16} />
                <Text>Voir</Text>
            </Pressable>

            <Pressable style={[styles.action, styles.disabled]}>
                <Ionicons name="create" size={16} />
                <Text>Modifier</Text>
            </Pressable>

            <Pressable style={[styles.action, styles.disabled]}>
                <Ionicons name="lock-closed" size={16} />
                <Text>Suspendre</Text>
            </Pressable>
        </View>
    );
}

const styles = StyleSheet.create({
    row: {
        flexDirection: "row",
        gap: 16,
        marginTop: 12,
    },
    action: {
        flexDirection: "row",
        gap: 6,
        alignItems: "center",
    },
    disabled: {
        opacity: 0.4,
    },
});
