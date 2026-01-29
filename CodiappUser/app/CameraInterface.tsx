import { View, Text, ImageBackground, StyleSheet, Pressable } from 'react-native';
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

export default function CameraInterface() {
    const router = useRouter();

    return (
        <View style={styles.container}>
            <ImageBackground
                source={require("../assets/images/exemple_camera.jpg")}
                style={styles.fullScreen}
                resizeMode="cover"
            >
                {/* BARRE SUPÉRIEURE */}
                <View style={styles.header}>
                    <Pressable onPress={() => router.back()} style={styles.backBtn}>
                        <Ionicons name="chevron-back" size={32} color="white" />
                    </Pressable>
                    <View style={styles.liveBadge}>
                        <Text style={styles.liveText}>AIDE EN COURS</Text>
                    </View>
                </View>

                {/* MENU DE DROITE */}
                <View style={styles.sideMenu}>
                    {/* Bouton Dessiner (Stylo) */}
                    <Pressable style={[styles.actionBtn, { backgroundColor: '#3B82F6' }]}>
                        <Ionicons name="pencil" size={28} color="white" />
                    </Pressable>

                    {/* Bouton Inverser Caméra */}
                    <Pressable style={[styles.actionBtn, { backgroundColor: '#A855F7' }]}>
                        <Ionicons name="camera-reverse" size={28} color="white" />
                    </Pressable>

                    {/* Bouton Chat */}
                    <Pressable
                        onPress={() => router.push('/ChatScreen')}
                        style={[styles.actionBtn, { backgroundColor: '#F97316' }]}
                    >
                        <Ionicons name="chatbubble-ellipses" size={28} color="white" />
                    </Pressable>
                </View>

                {/* BOUTON QUITTER (BAS) */}
                <View style={styles.footer}>
                    <Pressable
                        onPress={() => router.push('/ChatScreen')}
                        style={styles.stopBtn}
                    >
                        <Ionicons name="stop-circle" size={24} color="white" />
                        <Text style={styles.stopText}>TERMINER</Text>
                    </Pressable>
                </View>

            </ImageBackground>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    fullScreen: {
        flex: 1,
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
    liveBadge: {
        backgroundColor: '#EF4444',
        paddingHorizontal: 12,
        paddingVertical: 4,
        borderRadius: 8,
    },
    liveText: {
        color: 'white',
        fontWeight: 'bold',
        fontSize: 12,
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
        shadowOffset: { width: 0, height: 2 },
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