import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from 'react-native';

export default function MapInterface() {
    const router = useRouter();

    return (
        <View style={styles.container}>

            {/* CARTE */}
            <View style={styles.mapProvider}>
                <View style={styles.placeholderMap}>
                    <Ionicons name="map-outline" size={60} color="#ccc" />
                    <Text style={{ color: '#aaa', marginTop: 10 }}>Carte en attente d'API</Text>
                </View>
            </View>

            {/* Interface superposée sur la carte : contrôles flottants */}
            <View style={StyleSheet.absoluteFill} pointerEvents="box-none">

                {/* Header : bouton back + badge distance */}
                <View style={styles.header}>
                    <Pressable onPress={() => router.back()} style={styles.backBtn}>
                        <Ionicons name="chevron-back" size={32} color="white" />
                    </Pressable>

                    <View style={styles.distanceBadge}>
                        <Text style={styles.distanceLabel}>DISTANCE</Text>
                        <Text style={styles.distanceValue}>0.0 km</Text>
                    </View>
                </View>

                {/* Menu */}
                <View style={styles.sideMenu}>
                    <Pressable style={[styles.actionBtn, { backgroundColor: '#3B82F6' }]}>
                        <Ionicons name="locate" size={28} color="white" />
                    </Pressable>
                </View>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    mapProvider: {
        ...StyleSheet.absoluteFillObject,
    },
    placeholderMap: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#E5E7EB',
    },
    dot: {
        position: 'absolute',
        width: 16,
        height: 16,
        borderRadius: 8,
        borderWidth: 2,
        borderColor: 'white',
    },
    header: {
        paddingTop: 60,
        paddingHorizontal: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    backBtn: {
        padding: 8,
        backgroundColor: 'rgba(0,0,0,0.3)',
        borderRadius: 12,
    },
    distanceBadge: {
        backgroundColor: 'white',
        paddingHorizontal: 15,
        paddingVertical: 6,
        borderRadius: 12,
        alignItems: 'center',
        elevation: 4,
        shadowColor: '#000',
        shadowOpacity: 0.2,
        shadowRadius: 3,
    },
    distanceLabel: {
        fontSize: 10,
        fontWeight: 'bold',
        color: '#94A3B8',
    },
    distanceValue: {
        fontSize: 16,
        fontWeight: '900',
        color: '#1E293B',
    },
    sideMenu: {
        position: 'absolute',
        right: 20,
        top: '30%',
        gap: 15,
    },
    actionBtn: {
        width: 60,
        height: 60,
        borderRadius: 20,
        justifyContent: 'center',
        alignItems: 'center',
        elevation: 5,
        shadowColor: '#000',
        shadowOpacity: 0.3,
        shadowRadius: 5,
    },
    footer: {
        position: 'absolute',
        bottom: 50,
        width: '100%',
        alignItems: 'center',
    },
    stopBtn: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#EF4444',
        paddingHorizontal: 25,
        paddingVertical: 15,
        borderRadius: 20,
        gap: 10,
    },
    stopText: {
        color: 'white',
        fontWeight: 'bold',
        fontSize: 16,
    },
});