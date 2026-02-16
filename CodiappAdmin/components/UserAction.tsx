import { useRouter } from "expo-router";
import { StyleSheet, View } from "react-native";
import ActionButton from "./ActionButton";

export default function UserActions({ userId }: { userId?: string }) {
    const router = useRouter()

    return (
        <View style={styles.container}>
            {/* Ensemble de boutons d'action pour l'utilisateur */}
            <ActionButton icon="person-add" label="Attribuer un tuteur" />
            <ActionButton icon="map" label="Voir sur la carte" onPress={() => router.push("/MapInterface")} />
            <ActionButton
                icon="ban"
                label="Suspendre l’utilisateur"
            />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        paddingHorizontal: 16,
        marginBottom: 24,
    },
});
