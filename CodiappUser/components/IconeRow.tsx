import { View, StyleSheet } from "react-native";
import TelCard from "./TelCard";
import CameraCard from "./CameraCard";
import LocationCard from "./LocationCard";

export default function IconeRow() {
    return (
        // Rangée d'icônes rapides : appel, caméra, localisation
        <View style={styles.container}>
            <TelCard icon="call-outline" />
            <CameraCard icon="videocam-outline" />
            <LocationCard icon="location-outline" />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        marginTop: 8,
    },
});