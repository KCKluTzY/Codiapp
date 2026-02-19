import { View, Text, StyleSheet, Pressable, Image } from 'react-native';
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

export default function MapInterface() {
    const router = useRouter();

    return (
        <View style={styles.container}>

            {/* CARTE : placeholder en attendant l'intégration d'une vraie Map (MapView) */}
            <View style={styles.mapProvider}>
                <View style={styles.placeholderMap}>
                    <Image
                        source={require('../assets/images/map.png')}
                        style={styles.mapImage}
                        resizeMode="contain"

                    />

                </View>
            </View>

            {/* Interface flottante : contrôles superposés à la carte */}
            <View style={StyleSheet.absoluteFill} pointerEvents="box-none">

                {/* Header */}
                <View style={styles.header}>
                    <Pressable onPress={() => router.back()} style={styles.backBtn}>
                        <Ionicons name="chevron-back" size={32} color="white" />
                    </Pressable>

                    <View style={styles.distanceBadge}>
                        <Text style={styles.distanceLabel}>DISTANCE</Text>
                        <Text style={styles.distanceValue}>2.0 km</Text>
                    </View>
                </View>

                {/* Menu flottant : actions rapides (localisation, chat) */}
                <View style={styles.sideMenu}>
                    <Pressable style={[styles.actionBtn, { backgroundColor: '#3B82F6' }]}>
                        <Ionicons name="locate" size={28} color="white" />
                    </Pressable>

                    <Pressable
                        onPress={() => router.push('/ChatScreen')}
                        style={[styles.actionBtn, { backgroundColor: '#F97316' }]}
                    >
                        <Ionicons name="chatbubble-ellipses" size={28} color="white" />
                    </Pressable>
                </View>

                {/* Footer : bouton d'arrêt/fin d'intervention */}
                <View style={styles.footer}>
                    <Pressable
                        onPress={() => router.back()}
                        style={styles.stopBtn}
                    >
                        <Ionicons name="stop-circle" size={24} color="white" />
                        <Text style={styles.stopText}>TERMINER</Text>
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
        width: "100%",
        height: "100%",
        backgroundColor: "#E5E7EB",
    },
    mapImage: {
        width: "100%",
        height: "100%",
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
        top: '40%',
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