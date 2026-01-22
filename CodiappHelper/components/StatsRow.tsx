import { View, StyleSheet } from "react-native";
import StatsCard from "./StatsCard";

export default function StatsRow() {
    return (
        <View style={styles.container}>
            <StatsCard value={0} label="Demandes en cours" />
            <StatsCard value={5} label="Personnes suivis" />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        marginTop: 8,
    },
});