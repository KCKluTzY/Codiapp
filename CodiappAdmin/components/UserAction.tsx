import { View, StyleSheet } from "react-native";
import ActionButton from "./ActionButton";

export default function UserActions({ userId }: { userId?: string }) {
    return (
        <View style={styles.container}>
            <ActionButton icon="person-add" label="Attribuer un tuteur" />
            <ActionButton icon="map" label="Voir sur la carte" />
            <ActionButton
                icon="ban"
                label="Suspendre l’utilisateur"
                danger
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
