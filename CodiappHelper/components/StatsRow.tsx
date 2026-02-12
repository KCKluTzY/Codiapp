import { StyleSheet, View } from "react-native";
import StatsCard from "./StatsCard";

export default function StatsRow() {
    return (
        <View style={styles.container}>
            <StatsCard value={2} label="Aides en cours" />
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